package com.mackena.Banking_Application_backend.repository;

import com.mackena.Banking_Application_backend.models.entity.Transaction;
import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find by account with pagination
    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    // Find by account with filters (existing method - works fine)
    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.id = :accountId
      AND (:startDate IS NULL OR t.createdAt >= :startDate)
      AND (:endDate IS NULL OR t.createdAt <= :endDate)
      AND (:transactionType IS NULL OR t.transactionType = :transactionType)
      AND (:transactionDirection IS NULL OR t.transactionDirection = :transactionDirection)
      AND (:minAmount IS NULL OR t.amount >= :minAmount)
      AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
    ORDER BY t.createdAt DESC
""")
    Page<Transaction> findTransactionsWithFilters(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("transactionType") TransactionType transactionType,
            @Param("transactionDirection") TransactionDirection transactionDirection,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

    // Simple queries for all transactions - no complex filtering to avoid PostgreSQL issues
    @Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC")
    Page<Transaction> findAllTransactionsOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
SELECT t FROM Transaction t
WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
SELECT t FROM Transaction t
WHERE t.transactionType = :transactionType
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByType(
            @Param("transactionType") TransactionType transactionType,
            Pageable pageable
    );

    @Query("""
SELECT t FROM Transaction t
WHERE t.transactionDirection = :transactionDirection
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByDirection(
            @Param("transactionDirection") TransactionDirection transactionDirection,
            Pageable pageable
    );

    @Query("""
SELECT t FROM Transaction t
WHERE t.amount >= :minAmount AND t.amount <= :maxAmount
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByAmountRange(
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

    // Combined queries for common filter combinations
    @Query("""
SELECT t FROM Transaction t
WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate
  AND t.transactionType = :transactionType
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByDateRangeAndType(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("transactionType") TransactionType transactionType,
            Pageable pageable
    );

    @Query("""
SELECT t FROM Transaction t
WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate
  AND t.transactionDirection = :transactionDirection
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByDateRangeAndDirection(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("transactionDirection") TransactionDirection transactionDirection,
            Pageable pageable
    );

    @Query("""
SELECT t FROM Transaction t
WHERE t.transactionType = :transactionType
  AND t.transactionDirection = :transactionDirection
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findAllTransactionsByTypeAndDirection(
            @Param("transactionType") TransactionType transactionType,
            @Param("transactionDirection") TransactionDirection transactionDirection,
            Pageable pageable
    );

    // Find by transfer reference
    List<Transaction> findByTransferReference(String transferReference);

    // Find by reference number
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    // Monthly transaction summary
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId " +
            "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    long countTransactionsByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get transactions by transaction type for admin
    @Query("SELECT t FROM Transaction t WHERE t.transactionType = :transactionType ORDER BY t.createdAt DESC")
    Page<Transaction> findByTransactionType(@Param("transactionType") TransactionType transactionType, Pageable pageable);

    // Get all transactions ordered by date (for admin overview)
    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);


    // Account-specific queries with individual filters (PostgreSQL-friendly)
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.createdAt >= :startDate AND t.createdAt <= :endDate ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionType = :transactionType ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdAndTransactionType(
            @Param("accountId") Long accountId,
            @Param("transactionType") TransactionType transactionType,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionDirection = :transactionDirection ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdAndTransactionDirection(
            @Param("accountId") Long accountId,
            @Param("transactionDirection") TransactionDirection transactionDirection,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.amount >= :minAmount AND t.amount <= :maxAmount ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdAndAmountRange(
            @Param("accountId") Long accountId,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

    // Combined filters for common combinations
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.createdAt >= :startDate AND t.createdAt <= :endDate AND t.transactionType = :transactionType ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdAndDateRangeAndType(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("transactionType") TransactionType transactionType,
            Pageable pageable
    );
}