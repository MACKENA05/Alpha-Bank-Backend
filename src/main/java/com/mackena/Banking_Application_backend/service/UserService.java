package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.response.UserListResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.data.domain.Pageable;
import     com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    @Transactional(readOnly = true)
    UserResponse getUserProfile(Long id);

    @Transactional(readOnly = true)
    UserResponse getUserById(Long id);

    @Transactional(readOnly = true)
    UserListResponse getAllUsers(Pageable pageable);

    @Transactional
    DeleteUserResponse deleteUser(Long userId);

    @Transactional(readOnly = true)
    User findUserById(Long UserId);

    @Transactional(readOnly = true)
    User findUserByEmail(String email);
};
