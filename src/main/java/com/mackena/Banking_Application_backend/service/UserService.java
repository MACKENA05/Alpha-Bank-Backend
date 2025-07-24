package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;


public interface UserService  {
    AuthResponse registerUser(@Valid UserRegistrationRequest request);
    AuthResponse loginUser(@Valid UserRegistrationRequest request);

}
