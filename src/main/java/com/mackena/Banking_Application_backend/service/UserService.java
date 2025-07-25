package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.response.UserListResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.data.domain.Pageable;
import     com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    UserResponse getUserProfile(Long id);
    UserResponse getUserById(Long id);
    UserListResponse getAllUsers(Pageable pageable);
    DeleteUserResponse deleteUser(Long userId);
    User findUserById(Long UserId);
    User findUserByEmail(String email);
};
