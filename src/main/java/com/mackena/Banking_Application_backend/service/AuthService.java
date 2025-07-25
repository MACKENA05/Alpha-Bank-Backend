package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.LoginRequest;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserListResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.data.domain.Pageable;

public interface AuthService {
    AuthResponse registerUser(UserRegistrationRequest request);
    AuthResponse loginUser(LoginRequest request);
}