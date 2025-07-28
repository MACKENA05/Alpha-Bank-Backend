package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.LoginRequest;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserListResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    @Transactional
    AuthResponse registerUser(UserRegistrationRequest request);

    @Transactional
    AuthResponse loginUser(LoginRequest request);
}