package com.mackena.Banking_Application_backend.models.entity;


import com.mackena.Banking_Application_backend.models.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "accounts", indexes = {
        @Index(name = "idx_account_number", columnList = "account_number"),
        @Index(name = "idx_account_user", columnList = "user_id"),
        @Index(name = "idx_account_type", columnList = "account_type")
} )
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"user", "transactions"})
@ToString(exclude = {"user", "transactions"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number",unique = true,nullable = false,length = 12)
    private String accountNumber;

    @Enumerated
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(precision = 15, scale = 2,nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "transaction_pin", length = 4)
    private String transactionPin;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


////    generating account number
//
//    @PrePersist
//    private void generateAccountNumber(){
//        if (accountNumber == null) {
//            accountNumber = generateUniqueAccountNumber();
//        }
//    }
//
//    public String generateUniqueAccountNumber(){
//        long timestamp = System.currentTimeMillis();
//        int random = (int) (Math.random() * 1000);
//        return "ACC" + timestamp + String.format("%03d", random) ;
//    }
//
//    public void credit(BigDecimal amount) {
//        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
//            balance = balance.add(amount);
//        }
//    }
//
//    public void debit(BigDecimal amount) {
//        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
//            if(hasSufficientBalance(amount)){
//                this.balance = this.balance.subtract(amount);
//            }else {
//                throw new RuntimeException("Insufficient balance");
//            }
//        }
//    }
//
//    private boolean hasSufficientBalance(BigDecimal amount) {
//        return amount != null && this.balance.compareTo(amount) >= 0;
//    }
//
//    public boolean isLowBalance(BigDecimal threshold) {
//        return this.balance.compareTo(threshold != null ? threshold : new BigDecimal("100.00")) < 0;
//    }
//
//    public boolean isPinValid(String pin) {
//        return this.transactionPin != null && this.transactionPin.equals(pin);
//    }
//
//    public void addTransaction(Transaction transaction) {
//        transactions.add(transaction);
//        transaction.setAccount(this);
//    }
//
//
}
