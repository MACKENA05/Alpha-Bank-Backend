package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.LoginRequest;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;
import com.mackena.Banking_Application_backend.exceptions.EmailAlreadyExistsException;
import com.mackena.Banking_Application_backend.exceptions.InvalidCredentialsException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.AccountType;
import com.mackena.Banking_Application_backend.models.enums.UserRole;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.security.JwtTokenProvider;
import com.mackena.Banking_Application_backend.service.impl.AuthServiceImpl;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import com.mackena.Banking_Application_backend.util.generator.AccountNumberGenerator;
import com.mackena.Banking_Application_backend.util.helper.SecurityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private EntityConverter entityConverter;
    @Mock private AccountNumberGenerator accountNumberGenerator;
    @Mock private SecurityHelper securityHelper;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .confirmPin("1234")
                .transactionPin("1234")
                .phoneNumber("1234567890")
                .address("Nairobi")
                .initialDeposit(BigDecimal.valueOf(1000))
                .accountType(AccountType.SAVINGS)
                .build();

        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.USER);
        user.setAccounts(new ArrayList<>());

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(securityHelper.encodePassword(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(accountNumberGenerator.generateAccountNumber()).thenReturn("1234567890");
        when(securityHelper.encodePin(anyString())).thenReturn("encodedPin");
        when(jwtTokenProvider.generateToken(anyString(), anyList())).thenReturn("jwtToken");
        when(jwtTokenProvider.getJwtExpirationInMs()).thenReturn(3600000);
        when(entityConverter.toUserResponse(any(User.class))).thenReturn(null);

        AuthResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("User registered successfully", response.getMessage());
    }


    @Test
    void testRegisterUser_PinMismatch_ShouldThrow() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .transactionPin("1234")
                .confirmPin("0000")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
    }

    @Test
    void testRegisterUser_EmailExists_ShouldThrow() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("exists@example.com")
                .transactionPin("1234")
                .confirmPin("1234")
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerUser(request));
    }

    @Test
    void testLoginUser_Success() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setRole(UserRole.USER);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(anyString(), anyList())).thenReturn("jwtToken");
        when(jwtTokenProvider.getJwtExpirationInMs()).thenReturn(3600000);
        when(entityConverter.toUserResponse(any(User.class))).thenReturn(null);

        AuthResponse response = authService.loginUser(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void testLoginUser_InvalidEmail_ShouldThrow() {
        LoginRequest request = new LoginRequest("invalid@example.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException());

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(request));
    }

    @Test
    void testLoginUser_AccountDisabled_ShouldThrow() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(false);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(request));
    }
}
