package com.mackena.Banking_Application_backend.dto.response;

import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserResponse {
    private String message;
    private UserResponse deletedUser;
    private BigDecimal totalBalanceReturned;
    private int accountsClosed;
}