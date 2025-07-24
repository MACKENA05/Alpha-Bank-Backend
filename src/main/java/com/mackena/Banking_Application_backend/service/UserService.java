package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.LoginRequest;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;

public interface UserService {
    AuthResponse registerUser(UserRegistrationRequest request);
    AuthResponse loginUser(LoginRequest request);
}