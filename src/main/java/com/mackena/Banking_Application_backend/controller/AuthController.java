package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.ApiResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    //inject userService bean
//    @Autowired
//    private final UserServiceImpl userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
//            @Valid @RequestBody UserRegistrationRequest request) {
//
//        return userService.registerUser(request);
//    };

}
