package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.mapper.UserMapper;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.security.JwtTokenProvider;
import com.mackena.Banking_Application_backend.service.UserService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import com.mackena.Banking_Application_backend.util.generator.AccountNumberGenerator;
import com.mackena.Banking_Application_backend.util.formatter.DateTimeUtil;
import com.mackena.Banking_Application_backend.util.generator.UserIdGenerator;
import com.mackena.Banking_Application_backend.util.helper.SecurityHelper;
import com.mackena.Banking_Application_backend.util.validator.PinValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityConverter entityConverter;
    private final UserIdGenerator userIdGenerator;
    private final AccountNumberGenerator accountNumberGenerator;
    private final SecurityHelper securityHelper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse registerUser(UserRegistrationRequest request) {

        //Validate pin confirmation
//        if(!request.getTransactionPin())


        return null;
    }

    @Override
    public AuthResponse loginUser(UserRegistrationRequest request) {
        return null;
    }
}
