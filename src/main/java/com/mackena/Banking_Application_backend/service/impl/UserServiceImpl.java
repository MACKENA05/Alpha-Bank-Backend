package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.exceptions.UserHasActiveBalanceException;
import com.mackena.Banking_Application_backend.exceptions.UserNotFoundException;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.service.UserService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import org.springframework.data.domain.Page;
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


    @Override
    public UserResponse getUserProfile(Long id) {
        User user = findUserById(id);
        return userConverter.toUserResponse(user);
    }

    @Override
    public User findUserById(Long userId) {
        log.info("Finding user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }


    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return userConverter.toUserResponse(user);
    }
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("Getting all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }



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
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}
