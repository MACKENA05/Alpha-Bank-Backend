package com.mackena.Banking_Application_backend.repository;


import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import com.mackena.Banking_Application_backend.models.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //find by account using pagination
    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    //find accounts with filters
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:transactionDirection IS NULL OR t.transactionDirection = :transactionDirection) " +
            "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR t.amount <= :maxAmount)")
    Page<Transaction> findTransactionsWithFilters(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("transactionType") TransactionType transactionType,
            @Param("transactionDirection") TransactionDirection transactionDirection,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable);

    //find by ref number
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    //find by transaction ref
    Optional<Transaction> findByTransferReference(String transferReference);

    //monthly transaction summary
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId " +
            "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    long countTransactionsByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
}
