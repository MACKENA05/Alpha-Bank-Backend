package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.mapper.UserMapper;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.service.UserService;
import com.mackena.Banking_Application_backend.util.generator.AccountNumberGenerator;
import com.mackena.Banking_Application_backend.util.formatter.DateTimeUtil;
import com.mackena.Banking_Application_backend.util.validator.PinValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;
    private final DateTimeUtil dateTimeUtil;
    private final PinValidator pinValidator;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        return null;
    }
}
