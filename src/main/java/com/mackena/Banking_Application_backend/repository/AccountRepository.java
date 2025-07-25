package com.mackena.Banking_Application_backend.repository;

import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    //Accounts methods

    //getting an account
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);

    List<Account> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT a FROM Account a WHERE a.balance < :threshold AND a.isActive = true ORDER BY a.balance ASC")
    List<Account> findAccountsWithLowBalance(@Param("threshold") BigDecimal threshold);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.isActive = true")
    BigDecimal getTotalSystemBalance();

    @Query("SELECT COUNT(a) FROM Account a WHERE a.isActive = true")
    long countActiveAccounts();

    @Query("SELECT COUNT(DISTINCT a.user.id) FROM Account a WHERE a.isActive = true")
    long countUsersWithActiveAccounts();

    @Query("SELECT AVG(a.balance) FROM Account a WHERE a.isActive = true")
    BigDecimal getAverageBalance();

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(t) FROM Account a JOIN a.transactions t WHERE a.id = :accountId")
    int getTransactionCountByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t.amount FROM Account a JOIN a.transactions t WHERE a.id = :accountId ORDER BY t.createdAt DESC LIMIT 1")
    BigDecimal getLastTransactionAmount(@Param("accountId") Long accountId);
}