package com.mackena.Banking_Application_backend.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private UserResponse user;
    private String message;
    private Long expiresIn; // Token expiration in seconds
    private LocalDateTime expiresAt; // Exact expiration time
}