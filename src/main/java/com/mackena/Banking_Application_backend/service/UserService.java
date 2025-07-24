package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import jakarta.validation.Valid;

public interface UserService  {

    public UserResponse registerUser(@Valid UserRegistrationRequest request);

}
