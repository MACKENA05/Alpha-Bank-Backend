package com.mackena.Banking_Application_backend.models.entity;

import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionStatus;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_reference", columnList = "reference_number"),
        @Index(name = "idx_transaction_account", columnList = "account_id"),
        @Index(name = "idx_transaction_type", columnList = "transaction_type"),
        @Index(name = "idx_transaction_status", columnList = "status"),
        @Index(name = "idx_transaction_date", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"account"})
@ToString(exclude = {"account"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_direction", nullable = false)
    private TransactionDirection transactionDirection;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "reference_number", unique = true, nullable = false, length = 20)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Generate reference number before persisting
    @PrePersist
    private void generateReferenceNumber() {
        if (referenceNumber == null) {
            referenceNumber = generateUniqueReferenceNumber();
        }
    }

    private String generateUniqueReferenceNumber() {
        // Generate format: TXN123456ABC (12 characters total)
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        return "TXN" + (timestamp % 1000000) + uuid;
    }

    // Utility methods
    public boolean isPending() {
        return TransactionStatus.PENDING.equals(this.status);
    }

    public boolean isCompleted() {
        return TransactionStatus.COMPLETED.equals(this.status);
    }

    public boolean isFailed() {
        return TransactionStatus.FAILED.equals(this.status);
    }

    public boolean isWithdrawal() {
        return TransactionType.WITHDRAW.equals(this.transactionType);
    }

    public boolean isDeposit() {
        return TransactionType.DEPOSIT.equals(this.transactionType);
    }

    public boolean isDebit() {
        return TransactionDirection.DEBIT.equals(this.transactionDirection);
    }

    public boolean isCredit() {
        return TransactionDirection.CREDIT.equals(this.transactionDirection);
    }

    public String getFormattedAmount() {
        String prefix = isDebit() ? "-" : "+";
        return prefix + "KES " + amount.toString();
    }
}