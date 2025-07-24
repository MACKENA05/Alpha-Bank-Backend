package com.mackena.Banking_Application_backend.dtos.mapper;


import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserSummaryResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

//    private final AccountMapper accountMapper;
//
////    public UserMapper(AccountMapper accountMapper) {
////        this.accountMapper = accountMapper;
////    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
//                .isActive(user.getIsActive())
//                .accounts(user.getAccounts().stream()
//                        .map(accountMapper::toSummaryResponse)
//                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserSummaryResponse toSummaryResponse(User user) {
        if (user == null) return null;

        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}