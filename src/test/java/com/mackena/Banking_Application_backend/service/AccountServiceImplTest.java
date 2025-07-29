package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;
import com.mackena.Banking_Application_backend.dtos.request.CreateAccountRequest;
import com.mackena.Banking_Application_backend.dtos.response.*;
import com.mackena.Banking_Application_backend.exceptions.*;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.AccountType;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.service.impl.AccountServiceImpl;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import com.mackena.Banking_Application_backend.util.generator.AccountNumberGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock private AccountRepository accountRepository;
    @Mock private EntityConverter accountConverter;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AccountNumberGenerator accountNumberGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User testUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setAccounts(new ArrayList<>());
        return user;
    }

    private Account testAccount(User user) {
        return Account.builder()
                .id(1L)
                .user(user)
                .accountNumber("ACC123")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .transactionPin("pin")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetUserAccounts_success() {
        User user = testUser();
        Account acc = testAccount(user);

        when(accountRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(user.getId())).thenReturn(List.of(acc));
        when(accountConverter.toAccountResponse(acc)).thenReturn(new AccountResponse());

        AccountListResponse response = accountService.getUserAccounts(user.getId());

        assertThat(response).isNotNull();
        assertThat(response.getTotalAccounts()).isEqualTo(1);
        assertThat(response.getTotalBalance()).isEqualByComparingTo("1000");
        verify(accountRepository).findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(user.getId());
    }

    @Test
    void testGetAccountByNumber_adminAccess() {
        User user = testUser();
        Account acc = testAccount(user);
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(acc));
        when(accountConverter.toAccountResponse(acc)).thenReturn(new AccountResponse());

        AccountResponse response = accountService.getAccountByNumber("ACC123", user.getId(), true);

        assertThat(response).isNotNull();
    }

    @Test
    void testGetAccountByNumber_nonOwnerThrows() {
        User owner = new User();
        owner.setId(10L);
        Account acc = testAccount(owner);

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(acc));

        assertThatThrownBy(() -> accountService.getAccountByNumber("ACC123", 99L, false))
                .isInstanceOf(AccountAccessDeniedException.class);
    }

    @Test
    void testGetLowBalanceAccounts_success() {
        Account acc = testAccount(testUser());
        acc.setBalance(BigDecimal.ONE);

        when(accountRepository.findAccountsWithLowBalance(BigDecimal.TEN)).thenReturn(List.of(acc));
        when(accountConverter.toAccountResponse(any())).thenReturn(new AccountResponse());

        LowBalanceAccountsResponse response = accountService.getLowBalanceAccounts(BigDecimal.TEN);

        assertThat(response).isNotNull();
        assertThat(response.getTotalLowBalance()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(response.getTotalLowBalanceAccounts()).isEqualTo(1);
    }

    @Test
    void testCreateAccountForUser_success() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setTransactionPin("1234");
        request.setConfirmPin("1234");
        request.setAccountType(AccountType.SAVINGS);
        request.setInitialDeposit(BigDecimal.valueOf(500));

        User user = testUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountNumberGenerator.generateAccountNumber()).thenReturn("ACC999");
        when(accountRepository.existsByAccountNumber("ACC999")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded-pin");
        when(accountRepository.save(any())).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setCreatedAt(LocalDateTime.now());
            return acc;
        });

        CreateAccountResponse response = accountService.createAccountForUser(request, user.getEmail());

        assertThat(response).isNotNull();
        assertThat(response.getAccountNumber()).isEqualTo("ACC999");
        assertThat(response.getBalance()).isEqualByComparingTo("500");
    }

    @Test
    void testCreateAccountForUser_pinMismatch() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setTransactionPin("1234");
        request.setConfirmPin("0000");

        assertThatThrownBy(() -> accountService.createAccountForUser(request, "test@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction PIN and confirmation PIN do not match");
    }

    @Test
    void testCreateAccountForUser_duplicateAccountType() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setTransactionPin("1234");
        request.setConfirmPin("1234");
        request.setAccountType(AccountType.SAVINGS);

        User user = testUser();
        Account acc = testAccount(user);
        user.getAccounts().add(acc);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> accountService.createAccountForUser(request, user.getEmail()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already has an active");
    }

    @Test
    void testFindAccountByNumber_notFound() {
        when(accountRepository.findByAccountNumber("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findAccountByNumber("missing"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void testValidateAccountOwnership_success() {
        User user = testUser();
        Account acc = testAccount(user);

        assertThatCode(() -> accountService.validateAccountOwnership(acc, user.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void testGetTotalSystemBalance() {
        when(accountRepository.getTotalSystemBalance()).thenReturn(BigDecimal.valueOf(1000));
        when(accountRepository.countActiveAccounts()).thenReturn(2L);
        when(accountRepository.countUsersWithActiveAccounts()).thenReturn(1L);

        TotalBalanceResponse response = accountService.getTotalSystemBalance();

        assertThat(response.getTotalSystemBalance()).isEqualByComparingTo("1000");
        assertThat(response.getAverageBalancePerAccount()).isEqualByComparingTo("500");
    }

    @Test
    void testGetAccountsByUser() {
        User user = testUser();
        Account acc = testAccount(user);

        when(accountRepository.findByUserAndIsActiveTrue(user)).thenReturn(List.of(acc));

        List<Account> results = accountService.getAccountsByUser(user);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAccountNumber()).isEqualTo("ACC123");
    }

    @Test
    void testSearchAccounts() {
        Account acc = testAccount(testUser());
        when(accountRepository.findByAccountNumberContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
                anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(List.of(acc));

        List<Account> results = accountService.searchAccounts("ACC", 0, 10);

        assertThat(results).hasSize(1);
        verify(accountRepository).findByAccountNumberContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
                "ACC", "ACC", "ACC", "ACC", PageRequest.of(0, 10));
    }

    @Test
    void testGetTotalAccountCount() {
        when(accountRepository.count()).thenReturn(5L);
        assertThat(accountService.getTotalAccountCount()).isEqualTo(5);
    }

    @Test
    void testGetActiveAccountCount() {
        when(accountRepository.countByIsActiveTrue()).thenReturn(4L);
        assertThat(accountService.getActiveAccountCount()).isEqualTo(4);
    }

    @Test
    void testFindAccountsWithBalanceBelow() {
        Account acc = testAccount(testUser());
        acc.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findByBalanceLessThanAndIsActiveTrue(BigDecimal.valueOf(100)))
                .thenReturn(List.of(acc));

        List<Account> results = accountService.findAccountsWithBalanceBelow(BigDecimal.valueOf(100));
        assertThat(results).hasSize(1);
    }

    @Test
    void testFindByAccountNumber_success() {
        Account acc = testAccount(testUser());
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(acc));

        Account result = accountService.findByAccountNumber("ACC123");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("ACC123");
    }

}
