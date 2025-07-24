package com.mackena.Banking_Application_backend.util.converter;

import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TransactionResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.util.formatter.CurrencyFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class EntityConverter {

    @Autowired
    private CurrencyFormatter currencyFormatter;

    private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("100.00");

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;

        UserResponse build = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .isActive(user.isActive())
                .accounts(user.getAccounts() != null ?
                        user.getAccounts().stream()
                                .map(this::toAccountResponse)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        return build;
    }

    public AccountResponse toAccountResponse(Account account) {
        if (account == null) return null;

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .formattedBalance(currencyFormatter.formatAmount(account.getBalance()))
                .active(account.isActive())
                .lowBalance(account.getBalance().compareTo(LOW_BALANCE_THRESHOLD) < 0)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

//    public TransactionResponse toTransactionResponse(Transaction transaction) {
//        if (transaction == null) return null;
//
//        return TransactionResponse.builder()
//                .id(transaction.getId())
//                .referenceNumber(transaction.getReferenceNumber())
//                .amount(transaction.getAmount())
//                .formattedAmount(currencyFormatter.formatAmount(transaction.getAmount()))
//                .transactionType(transaction.getTransactionType())
//                .description(transaction.getDescription())
//                .balanceAfter(transaction.getBalanceAfter())
//                .formattedBalanceAfter(currencyFormatter.formatAmount(transaction.getBalanceAfter()))
//                .accountNumber(transaction.getAccount() != null ?
//                        transaction.getAccount().getAccountNumber() : null)
//                .createdAt(transaction.getCreatedAt())
//                .timeAgo(formatTimeAgo(transaction.getCreatedAt()))
//                .build();
//    }

//    private String formatTimeAgo(LocalDateTime dateTime) {
//        if (dateTime == null) return "Unknown";
//
//        LocalDateTime now = LocalDateTime.now();
//        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
//        long hours = ChronoUnit.HOURS.between(dateTime, now);
//        long days = ChronoUnit.DAYS.between(dateTime, now);
//
//        if (minutes < 1) return "Just now";
//        if (minutes < 60) return minutes + " minutes ago";
//        if (hours < 24) return hours + " hours ago";
//        if (days < 30) return days + " days ago";
//
//        return dateTime.toLocalDate().toString();
//    }
//}

};
