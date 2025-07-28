package com.mackena.Banking_Application_backend.util.converter;

import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityConverter {

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;

        List<AccountResponse> validAccounts = user.getAccounts() != null
                ? user.getAccounts().stream()
                .map(this::toAccountResponse)
                .filter(accountResponse -> accountResponse != null && accountResponse.getAccountNumber() != null)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .accounts(validAccounts)
                .totalAccounts(validAccounts.size())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public AccountResponse toAccountResponse(Account account) {
        if (account == null || account.getAccountNumber() == null) return null;

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .isActive(account.isActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}