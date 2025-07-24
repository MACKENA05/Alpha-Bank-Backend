package com.mackena.Banking_Application_backend.service.impl;


import com.mackena.Banking_Application_backend.dtos.request.LoginRequest;
import com.mackena.Banking_Application_backend.dtos.request.UserRegistrationRequest;
import com.mackena.Banking_Application_backend.dtos.response.AuthResponse;
import com.mackena.Banking_Application_backend.exceptions.EmailAlreadyExistsException;
import com.mackena.Banking_Application_backend.exceptions.InvalidCredentialsException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.UserRole;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.security.JwtTokenProvider;
import com.mackena.Banking_Application_backend.service.UserService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import com.mackena.Banking_Application_backend.util.generator.AccountNumberGenerator;
import com.mackena.Banking_Application_backend.util.helper.SecurityHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityConverter entityConverter;
    private final AccountNumberGenerator accountNumberGenerator;
    private final SecurityHelper securityHelper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse registerUser(UserRegistrationRequest request) {
        log.info("Starting user registration for email: {}", request.getEmail());

        // Validate PIN confirmation
        validatePinConfirmation(request);

        // Check for existing email and phone
        validateEmailAndPhoneUniqueness(request);

        // Create and save user
        User user = buildUserFromRequest(request);
        User savedUser = userRepository.save(user);

        // Create account for user
        Account account = createAccountForUser(request, savedUser);
        savedUser.getAccounts().add(account);
        userRepository.save(savedUser);

        log.info("User registered successfully with ID: {} and Account: {}",
                savedUser.getId(), account.getAccountNumber());

        // Generate JWT token and build response
        return buildAuthResponse(savedUser, "User registered successfully");
    }


    @Transactional
    @Override
    public AuthResponse loginUser(LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());

        try {
            // Authenticate user using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve user from database
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

            // Check if the user account is enabled
            if (!user.isEnabled()) {
                throw new InvalidCredentialsException("Account is disabled. Please contact support.");
            }

            log.info("User logged in successfully: {}", user.getEmail());

            // Build and return the authentication response with JWT
            return buildAuthResponse(user, "Login successful");

        } catch (InvalidCredentialsException e) {
            log.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
    // Helper method to build AuthResponse with token and expiration
    private AuthResponse buildAuthResponse(User user, String message) {
        String email = user.getEmail();
        String role = "ROLE_" + user.getRole().name();
        List<String> roles = List.of(role);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(email, roles);

        // Calculate expiration time
        long expiresInSeconds = jwtTokenProvider.getJwtExpirationInMs() / 1000;
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expiresInSeconds);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(entityConverter.toUserResponse(user))
                .message(message)
                .expiresIn(expiresInSeconds)
                .expiresAt(expiresAt)
                .build();
    }

    private void validatePinConfirmation(UserRegistrationRequest request) {
        if (!request.getTransactionPin().equals(request.getConfirmPin())) {
            throw new IllegalArgumentException("Transaction PIN does not match confirmation PIN");
        }
    }

    private void validateEmailAndPhoneUniqueness(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new EmailAlreadyExistsException("Phone number already registered: " + request.getPhoneNumber());
        }
    }



    private User buildUserFromRequest(UserRegistrationRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setPassword(securityHelper.encodePassword(request.getPassword()));
        user.setEnabled(true);
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(UserRole.USER); // Assuming you have a Role enum
        }
        return user;
    }

    private Account createAccountForUser(UserRegistrationRequest request, User user) {
        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setUser(user);
        account.setAccountType(request.getAccountType());
        // Encode the PIN for security
        account.setTransactionPin(securityHelper.encodePin(request.getTransactionPin()));
        account.setBalance(request.getInitialDeposit());
        account.setActive(true); // Set account as active
        return account;
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generateAccountNumber();
        } while (isAccountNumberExists(accountNumber));
        return accountNumber;
    }

    private boolean isAccountNumberExists(String accountNumber) {
        return userRepository.findAll().stream()
                .flatMap(user -> user.getAccounts().stream())
                .anyMatch(account -> account.getAccountNumber().equals(accountNumber));
    }
}
