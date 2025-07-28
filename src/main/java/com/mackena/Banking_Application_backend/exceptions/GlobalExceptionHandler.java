package com.mackena.Banking_Application_backend.exceptions;

import com.mackena.Banking_Application_backend.dtos.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import com.mackena.Banking_Application_backend.dtos.response.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.error("User not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("User Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex, WebRequest request) {
        log.error("Email already exists: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Email Already Exists")
                .status(HttpStatus.CONFLICT.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request) {
        log.error("Invalid credentials: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Invalid Credentials")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPin(InvalidPinException ex, WebRequest request) {
        log.error("Invalid PIN: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Invalid PIN")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex, WebRequest request) {
        log.error("Insufficient funds: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Insufficient Funds")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccount(InvalidAccountException ex, WebRequest request) {
        log.error("Invalid account error: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Account")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(TransactionNotFoundException ex, WebRequest request) {
        log.error("Transaction not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Transaction Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed: " + errors)
                .error("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Illegal Argument")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

    // NEW HANDLERS ADDED BELOW FOR TRANSACTION/TRANSFER ISSUES

    @ExceptionHandler(IllegalTransactionStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalTransactionState(IllegalTransactionStateException e, WebRequest request) {
        log.error("Illegal transaction state: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Transaction failed due to invalid state. Please try again.")
                .error("Transaction State Error")
                .status(HttpStatus.CONFLICT.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystem(TransactionSystemException e, WebRequest request) {
        log.error("Transaction system error: {}", e.getMessage(), e);

        // Check if it's caused by a validation issue
        Throwable rootCause = e.getRootCause();
        String message = "Transaction processing failed";

        if (rootCause != null) {
            if (rootCause instanceof IllegalStateException) {
                message = "Invalid transaction state: " + rootCause.getMessage();
            } else if (rootCause instanceof IllegalArgumentException) {
                message = "Invalid transaction parameters: " + rootCause.getMessage();
            }
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error("Transaction System Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException e, WebRequest request) {
        log.error("Database access error: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Database operation failed. Please try again.")
                .error("Database Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e, WebRequest request) {
        log.error("Data integrity violation: {}", e.getMessage(), e);

        String message = "Data integrity violation occurred";

        // Check for common constraint violations
        if (e.getMessage() != null) {
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("duplicate") || errorMsg.contains("unique")) {
                message = "Duplicate data detected. Operation cannot be completed.";
            } else if (errorMsg.contains("foreign key") || errorMsg.contains("reference")) {
                message = "Referenced data does not exist or cannot be modified.";
            } else if (errorMsg.contains("check constraint")) {
                message = "Data validation failed. Please check your input.";
            }
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error("Data Integrity Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, WebRequest request) {
        log.error("Runtime error: {}", e.getMessage(), e);

        // Don't expose internal error details in production
        String message = "An unexpected error occurred during processing";

        // For transfer-related runtime exceptions, provide more specific messages
        if (e.getMessage() != null) {
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("transfer") || errorMsg.contains("transaction")) {
                message = "Transfer processing failed. Please verify your details and try again.";
            } else if (errorMsg.contains("account")) {
                message = "Account operation failed. Please check account details.";
            } else if (errorMsg.contains("balance")) {
                message = "Balance operation failed. Please try again.";
            }
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error("Runtime Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("An unexpected error occurred. Please try again later.")
                .error("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}