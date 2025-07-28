package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.response.UserResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.service.UserService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;
    private final EntityConverter entityConverter; // Inject EntityConverter

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.info("Admin requesting user details for ID: {}", userId);

        try {
            User user = userService.findUserById(userId);
            UserResponse response = entityConverter.toUserResponse(user); // Use EntityConverter
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        log.info("Admin requesting all users - page: {}, size: {}, sortBy: {}, sortDirection: {}",
                page, size, sortBy, sortDirection);

        try {
            // Validate sort direction
            Sort.Direction direction;
            try {
                direction = Sort.Direction.fromString(sortDirection.toUpperCase());
            } catch (Exception e) {
                log.warn("Invalid sort direction '{}', defaulting to ASC", sortDirection);
                direction = Sort.Direction.ASC;
            }

            String validSortBy = validateSortField(sortBy);

            Sort sort = Sort.by(direction, validSortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<User> users = getUsersPage(pageable);
            Page<UserResponse> userResponses = users.map(entityConverter::toUserResponse); // Use EntityConverter

            log.info("Successfully retrieved {} users", users.getTotalElements());
            return ResponseEntity.ok(userResponses);

        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve users: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        log.info("Admin requesting to delete user ID: {}", userId);

        try {
            userService.deleteUser(userId);
            log.info("Successfully deleted user ID: {}", userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    private String validateSortField(String sortBy) {
        List<String> validFields = List.of("id", "email", "firstName", "lastName", "createdAt", "role");

        if (validFields.contains(sortBy)) {
            return sortBy;
        } else {
            log.warn("Invalid sort field '{}', defaulting to 'id'", sortBy);
            return "id";
        }
    }

    private Page<User> getUsersPage(Pageable pageable) {
        try {
            return userService.getAllUsers(pageable);
        } catch (Exception e) {
            log.warn("Falling back to list-based user retrieval");
            List<User> allUsers = (List<User>) userService.getAllUsers(pageable);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), allUsers.size());
            List<User> pageContent = allUsers.subList(start, end);
            return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allUsers.size());
        }
    }
}