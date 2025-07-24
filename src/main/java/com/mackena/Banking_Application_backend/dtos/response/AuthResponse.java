package com.mackena.Banking_Application_backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserResponse user;
    private String Message;
}
