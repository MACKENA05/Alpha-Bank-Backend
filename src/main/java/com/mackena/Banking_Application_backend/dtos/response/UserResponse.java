package com.mackena.Banking_Application_backend.dtos.response;


import com.mackena.Banking_Application_backend.models.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String address;
    private UserRole role;
    private Boolean isActive;
    private List<AccountSummaryResponse> accounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
