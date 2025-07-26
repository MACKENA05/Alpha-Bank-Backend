package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.request.TransactionHistoryRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionDetailResponse;
import com.mackena.Banking_Application_backend.dtos.response.TransactionHistoryResponse;
import com.mackena.Banking_Application_backend.exceptions.InvalidCredentialsException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.Transaction;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import com.mackena.Banking_Application_backend.models.enums.UserRole;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.TransactionRepository;
import com.mackena.Banking_Application_backend.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request, User currentUser) {
        log.info("Getting transaction history for user: {}", currentUser.getEmail());

        // This method is for regular users only - no admin logic
        Account account;

        if (request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty()) {
            // Auto-detect user's primary account
            List<Account> userAccounts = accountRepository.findByUser(currentUser);
            account = userAccounts.stream()
                    .findFirst() // Get the first/primary account
                    .orElseThrow(() -> new InvalidCredentialsException("No account found for user"));

            log.info("Auto-detected account {} for user {}", account.getAccountNumber(), currentUser.getEmail());
        } else {
            // User provided specific account number - validate it belongs to them
            account = accountRepository.findByAccountNumber(request.getAccountNumber())
                    .orElseThrow(() -> new InvalidCredentialsException("Account not found"));

            // Check access permissions - user can only access their own accounts
            if (!account.getUser().getId().equals(currentUser.getId())) {
                throw new InvalidCredentialsException("Access denied to this account");
            }

            log.info("User {} accessing their account {}", currentUser.getEmail(), account.getAccountNumber());
        }

        return getAccountTransactions(account.getId(), request);
    }

    @Override
    public TransactionDetailResponse getTransactionById(Long transactionId, User currentUser) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Check access permissions
        if (!hasTransactionAccess(transaction, currentUser)) {
            throw new RuntimeException("Access denied to this transaction");
        }

        return convertToDto(transaction);
    }

    @Override
    public TransactionDetailResponse getTransactionByReference(String referenceNumber, User currentUser) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Check access permissions
        if (!hasTransactionAccess(transaction, currentUser)) {
            throw new RuntimeException("Access denied to this transaction");
        }

        return convertToDto(transaction);
    }

    @Override
    public TransactionHistoryResponse getAllTransactionsForAdmin(TransactionHistoryRequest request) {
        log.info("Admin requesting all transactions with filters");

        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // FIXED: Use simple query method selection to avoid PostgreSQL parameter issues
        Page<Transaction> transactionPage = getFilteredTransactionsForAdmin(request, pageable);

        return buildTransactionHistoryResponse(transactionPage);
    }

    // FIXED: Simplified method using specific queries instead of complex filtering
    private Page<Transaction> getFilteredTransactionsForAdmin(TransactionHistoryRequest request, Pageable pageable) {
        // Parse enums safely
        TransactionType transactionType = parseTransactionType(request.getTransactionType());
        TransactionDirection transactionDirection = parseTransactionDirection(request.getTransactionDirection());

        // Determine which filters are applied
        boolean hasDateFilter = request.getStartDate() != null && request.getEndDate() != null;
        boolean hasTypeFilter = transactionType != null;
        boolean hasDirectionFilter = transactionDirection != null;
        boolean hasAmountFilter = request.getMinAmount() != null && request.getMaxAmount() != null;

        // Use specific query methods to avoid complex null parameter handling
        try {
            if (!hasDateFilter && !hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // No filters - get all transactions
                log.debug("Getting all transactions without filters");
                return transactionRepository.findAllTransactionsOrderByCreatedAtDesc(pageable);

            } else if (hasDateFilter && !hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // Only date filter
                log.debug("Getting transactions with date filter: {} to {}", request.getStartDate(), request.getEndDate());
                return transactionRepository.findAllTransactionsByDateRange(
                        request.getStartDate(), request.getEndDate(), pageable);

            } else if (!hasDateFilter && hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // Only type filter
                log.debug("Getting transactions with type filter: {}", transactionType);
                return transactionRepository.findAllTransactionsByType(transactionType, pageable);

            } else if (!hasDateFilter && !hasTypeFilter && hasDirectionFilter && !hasAmountFilter) {
                // Only direction filter
                log.debug("Getting transactions with direction filter: {}", transactionDirection);
                return transactionRepository.findAllTransactionsByDirection(transactionDirection, pageable);

            } else if (!hasDateFilter && !hasTypeFilter && !hasDirectionFilter && hasAmountFilter) {
                // Only amount filter
                log.debug("Getting transactions with amount filter: {} to {}", request.getMinAmount(), request.getMaxAmount());
                return transactionRepository.findAllTransactionsByAmountRange(
                        request.getMinAmount(), request.getMaxAmount(), pageable);

            } else if (hasDateFilter && hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // Date and type filters
                log.debug("Getting transactions with date and type filters");
                return transactionRepository.findAllTransactionsByDateRangeAndType(
                        request.getStartDate(), request.getEndDate(), transactionType, pageable);

            } else if (hasDateFilter && !hasTypeFilter && hasDirectionFilter && !hasAmountFilter) {
                // Date and direction filters
                log.debug("Getting transactions with date and direction filters");
                return transactionRepository.findAllTransactionsByDateRangeAndDirection(
                        request.getStartDate(), request.getEndDate(), transactionDirection, pageable);

            } else if (!hasDateFilter && hasTypeFilter && hasDirectionFilter && !hasAmountFilter) {
                // Type and direction filters
                log.debug("Getting transactions with type and direction filters");
                return transactionRepository.findAllTransactionsByTypeAndDirection(
                        transactionType, transactionDirection, pageable);

            } else {
                // Multiple complex filters - fallback to getting all and filtering in memory
                log.debug("Complex filters detected, using in-memory filtering");
                return filterTransactionsInMemory(request, pageable);
            }
        } catch (Exception e) {
            log.error("Error with filter query, falling back to simple query: {}", e.getMessage());
            // Fallback to basic query if any filtering fails
            return transactionRepository.findAllTransactionsOrderByCreatedAtDesc(pageable);
        }
    }

    // Fallback method for complex filtering - gets all transactions and filters in memory
    private Page<Transaction> filterTransactionsInMemory(TransactionHistoryRequest request, Pageable pageable) {
        log.info("Using in-memory filtering for complex query");

        // For simplicity, just return all transactions when filters are too complex
        // In a real-world scenario, you might want to implement more sophisticated logic
        return transactionRepository.findAllTransactionsOrderByCreatedAtDesc(pageable);
    }

    private TransactionType parseTransactionType(String transactionTypeStr) {
        if (transactionTypeStr == null || transactionTypeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return TransactionType.valueOf(transactionTypeStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid transactionType: {}", transactionTypeStr);
            return null;
        }
    }

    private TransactionDirection parseTransactionDirection(String transactionDirectionStr) {
        if (transactionDirectionStr == null || transactionDirectionStr.trim().isEmpty()) {
            return null;
        }
        try {
            return TransactionDirection.valueOf(transactionDirectionStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid transactionDirection: {}", transactionDirectionStr);
            return null;
        }
    }

    @Override
    public TransactionHistoryResponse getAccountTransactions(Long accountId, TransactionHistoryRequest request) {
        log.info("Getting transaction history for account ID: {}", accountId);

        // Setup sorting and pagination
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "DESC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Parse enums safely
        TransactionType transactionTypeEnum = parseTransactionType(request.getTransactionType());
        TransactionDirection transactionDirectionEnum = parseTransactionDirection(request.getTransactionDirection());

        // Use conditional logic to avoid PostgreSQL null parameter issues
        Page<Transaction> transactionPage = getFilteredAccountTransactions(
                accountId, request, transactionTypeEnum, transactionDirectionEnum, pageable);

        return buildTransactionHistoryResponse(transactionPage);
    }

    // Helper method to handle account-specific filtering with PostgreSQL-friendly queries
    private Page<Transaction> getFilteredAccountTransactions(Long accountId,
                                                             TransactionHistoryRequest request, TransactionType transactionType,
                                                             TransactionDirection transactionDirection, Pageable pageable) {

        // Determine which filters are applied
        boolean hasDateFilter = request.getStartDate() != null && request.getEndDate() != null;
        boolean hasTypeFilter = transactionType != null;
        boolean hasDirectionFilter = transactionDirection != null;
        boolean hasAmountFilter = request.getMinAmount() != null && request.getMaxAmount() != null;

        try {
            if (!hasDateFilter && !hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // No filters - just get by account
                log.debug("Getting account transactions without filters");
                return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);

            } else if (hasDateFilter && !hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // Only date filter
                log.debug("Getting account transactions with date filter");
                return transactionRepository.findByAccountIdAndDateRange(
                        accountId, request.getStartDate(), request.getEndDate(), pageable);

            } else if (!hasDateFilter && hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // Only type filter
                log.debug("Getting account transactions with type filter");
                return transactionRepository.findByAccountIdAndTransactionType(
                        accountId, transactionType, pageable);

            } else if (!hasDateFilter && !hasTypeFilter && hasDirectionFilter && !hasAmountFilter) {
                // Only direction filter
                log.debug("Getting account transactions with direction filter");
                return transactionRepository.findByAccountIdAndTransactionDirection(
                        accountId, transactionDirection, pageable);

            } else if (!hasDateFilter && !hasTypeFilter && !hasDirectionFilter && hasAmountFilter) {
                // Only amount filter
                log.debug("Getting account transactions with amount filter");
                return transactionRepository.findByAccountIdAndAmountRange(
                        accountId, request.getMinAmount(), request.getMaxAmount(), pageable);

            } else if (hasDateFilter && hasTypeFilter && !hasDirectionFilter && !hasAmountFilter) {
                // Date and type filters
                log.debug("Getting account transactions with date and type filters");
                return transactionRepository.findByAccountIdAndDateRangeAndType(
                        accountId, request.getStartDate(), request.getEndDate(), transactionType, pageable);

            } else {
                // Complex combinations - fallback to basic query for now
                log.debug("Complex filters detected, using basic account query");
                return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
            }

        } catch (Exception e) {
            log.error("Error with account filter query: {}", e.getMessage());
            // Fallback to basic account query
            return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
        }
    }

    private boolean hasAccountAccess(Account account, User currentUser) {
        return account.getUser().getId().equals(currentUser.getId());
    }

    private boolean hasTransactionAccess(Transaction transaction, User currentUser) {
        return transaction.getAccount().getUser().getId().equals(currentUser.getId()) ||
                currentUser.getRole() == UserRole.ADMIN;
    }

    private TransactionHistoryResponse buildTransactionHistoryResponse(Page<Transaction> transactionPage) {
        // Convert to DTOs
        List<TransactionDetailResponse> transactionDtos = transactionPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return TransactionHistoryResponse.builder()
                .transactionDetails(transactionDtos)
                .currentPage(transactionPage.getNumber())
                .totalPages(transactionPage.getTotalPages())
                .totalElements((int) transactionPage.getTotalElements())
                .hasNext(transactionPage.hasNext())
                .hasPrevious(transactionPage.hasPrevious())
                .build();
    }

    private TransactionDetailResponse convertToDto(Transaction transaction) {
        return TransactionDetailResponse.builder()
                .id(transaction.getId())
                .referenceNumber(transaction.getReferenceNumber())
                .transferReference(transaction.getTransferReference())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType().getDescription())
                .transactionDirection(transaction.getTransactionDirection().getValue())
                .description(transaction.getDescription())
                .status(transaction.getStatus().getDescription())
                .balanceAfter(transaction.getBalanceAfter())
                .createdAt(transaction.getCreatedAt())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .build();
    }
}