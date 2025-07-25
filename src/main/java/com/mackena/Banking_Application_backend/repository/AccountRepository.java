//package com.mackena.Banking_Application_backend.repository;
//
//import com.mackena.Banking_Application_backend.models.entity.Account;
//import com.mackena.Banking_Application_backend.models.entity.User;
//import com.mackena.Banking_Application_backend.models.enums.AccountType;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface AccountRepository extends JpaRepository<Account, Long> {
//
//    //Accounts methods
//
//    //getting an account
//    Optional<Account> findByAccountNumber(String accountNumber);
//
//    //validate account number
//    boolean existsByAccountNumber(String accountNumber);
//
//    //list account for given user
//    List<Account> findByUser(User user);
//
//    List<Account> findByUserId(Long userId);
//
//    //Lists accountType
//    List<Account> findByAccountType(AccountType accountType);
//
//    //List account types for specific user
//    List<Account> findByUserAndAccountType(User user, AccountType accountType);
//
//    //all active accounts
//    List<Account> findByIsActiveTrue();
//
//    //Active accounts for a specific user
//    List<Account> findByUserAndIsActive(User user, Boolean isActive);
//
//    List<Account> findByBalanceLessThanAndActiveTrue(BigDecimal threshold);
//
//    //Queries
//
//    @Query("SELECT a FROM Account a WHERE a.balance < :threshold AND a.isActive = true")
//    List<Account> findLowBalanceAccounts(@Param("threshold") BigDecimal threshold);
//
//
//    @Query("SELECT a FROM Account a WHERE a.balance >= :minBalance AND a.balance <= :maxBalance")
//    List<Account> findAccountsByBalanceRange(@Param("minBalance") BigDecimal minBalance,
//                                             @Param("maxBalance") BigDecimal maxBalance);
//
//    //show total balance accross all active accounts
//    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.isActive = true")
//    BigDecimal getTotalActiveBalance();
//
//    //count account of a specific type
//    long countByAccountType(AccountType accountType);
//
//    //count account that are active and inactive
//    Long countByIsActive(Boolean isActive);
//
//    //Track new opening accounts
//    @Query("SELECT COUNT(a) FROM Account a WHERE a.createdAt >= :date")
//    long countAccountsCreatedAfter(@Param("date") LocalDateTime date);
//
//    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
//    long countActiveAccountsByUserId(@Param("userId") Long userId);
//
//    // Pagination queries
//    Page<Account> findByAccountType(AccountType accountType, Pageable pageable);
//
//    Page<Account> findByIsActive(Boolean isActive, Pageable pageable);
//
//    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
//    Page<Account> findByUserId(@Param("userId") Long userId, Pageable pageable);
//
//
//
//
//
//
//}
