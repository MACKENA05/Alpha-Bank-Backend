package com.mackena.Banking_Application_backend.exceptions;

import com.mackena.Banking_Application_backend.dtos.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import com.mackena.Banking_Application_backend.dto.response.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException e, WebRequest request) {
        log.error("Account not found: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .error("Account Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccountAccessDenied(AccountAccessDeniedException e, WebRequest request) {
        log.error("Account access denied: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .error("Account Access Denied")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.error("Email already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPin(InvalidPinException ex) {
        log.error("Invalid PIN: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientFunds(InsufficientFundsException ex) {
        log.error("Insufficient funds: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccount(InvalidAccountException ex) {
        log.error("Invalid account error: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Account")
                .message(ex.getMessage())
                .path("/api/transactions")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTransactionNotFound(TransactionNotFoundException ex) {
        log.error("Transaction not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed: " + errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }

    @ExceptionHandler(UserHasActiveBalanceException.class)
    public ResponseEntity<ErrorResponse> handleUserHasActiveBalance(UserHasActiveBalanceException e, WebRequest request) {
        log.error("User has active balance: {}", e.getMessage());

        // Safe formatting of the balance
        String balanceMessage;
        try {
            Object totalBalance = e.getTotalBalance();
            if (totalBalance != null) {
                // Convert to string safely without any formatting flags
                balanceMessage = e.getMessage() + " Total balance: KES " + String.valueOf(totalBalance);
            } else {
                balanceMessage = e.getMessage() + " Total balance: KES 0.00";
            }
        } catch (Exception formatException) {
            log.warn("Error formatting balance in exception handler", formatException);
            balanceMessage = e.getMessage() + " (Balance information unavailable)";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(balanceMessage)
                .error("User Has Active Balance")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e, WebRequest request) {
        log.error("Access denied: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Access denied. You don't have permission to access this resource.")
                .error("Access Denied")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e, WebRequest request) {
        log.error("Illegal state: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .error("Illegal State")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }



}