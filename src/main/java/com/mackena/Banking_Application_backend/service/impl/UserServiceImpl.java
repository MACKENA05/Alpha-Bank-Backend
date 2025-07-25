package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserListResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.exceptions.UserHasActiveBalanceException;
import com.mackena.Banking_Application_backend.exceptions.UserNotFoundException;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.service.UserService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityConverter userConverter;

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserProfile(Long id) {
        User user = findUserById(id);
        return userConverter.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return userConverter.toUserResponse(user);
    }

    @Override
    public UserListResponse getAllUsers(Pageable pageable) {

        Page<User> userPage = userRepository.findAll(pageable);

        Page<UserResponse> userResponsePage = userPage.map(userConverter::toUserResponse);
        return UserListResponse.from(userResponsePage);
    }


    @Transactional
    @Override
    public DeleteUserResponse deleteUser(Long userId) {

        User user = findUserById(userId);
        //checking if user has a active balance
        BigDecimal totalBalance = userRepository.getTotalBalanceByUserId(userId);
        if(totalBalance != null && totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            throw new UserHasActiveBalanceException(
                    String.format("Cannot delete user %s % s.user has an active balance of Kes %.2f",
                            user.getFirstName(), user.getLastName(), totalBalance)
            );
        }
        //Get account count  before deleting
        int accountCount = userRepository.getActiveAccountCountByUserId(userId);

        //convert to response before deletion
        UserResponse userResponse = userConverter.toUserResponse(user);

        userRepository.delete(user);

        return DeleteUserResponse.builder()
                .message("User Deleted Successful")
                .deletedUser(userResponse)
                .totalBalanceReturned(totalBalance != null ? totalBalance : BigDecimal.ZERO)
                .accountsClosed(accountCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}
