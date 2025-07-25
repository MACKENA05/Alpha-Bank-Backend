// UserController.java - User management controller
package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.response.UserListResponse;
import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.security.CurrentUser;
import com.mackena.Banking_Application_backend.security.UserPrincipal;
import com.mackena.Banking_Application_backend.service.impl.UserServiceImpl;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.mackena.Banking_Application_backend.dto.response.DeleteUserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@CurrentUser UserDetails userDetails) {

        User user = userService.findUserByEmail(userDetails.getUsername());
        UserResponse response = userService.getUserProfile(user.getId());

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "createdAt")String sortBy,
            @RequestParam(defaultValue = "desc")String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        UserListResponse response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable Long userId) {
        log.info("Admin deleting user with ID: {}", userId);
        DeleteUserResponse response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

};