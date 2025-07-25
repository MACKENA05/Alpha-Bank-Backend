package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.request.TransactionHistoryRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionDetailResponse;
import com.mackena.Banking_Application_backend.dtos.response.TransactionHistoryResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.Transaction;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.TransactionRepository;
import com.mackena.Banking_Application_backend.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request, User currentUser) {

        // Validate account access
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().isAdmin()) {
            throw new RuntimeException("Access denied to this account");
        }

        // Create pageable with sorting
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Convert enum parameters
        TransactionType transactionType = request.getTransactionType() != null ?
                TransactionType.valueOf(request.getTransactionType()) : null;
        TransactionDirection transactionDirection = request.getTransactionDirection() != null ?
                TransactionDirection.valueOf(request.getTransactionDirection()) : null;

        // Get filtered transactions
        Page<Transaction> transactionPage = transactionRepository.findTransactionsWithFilters(
                account.getId(),
                request.getStartDate(),
                request.getEndDate(),
                transactionType,
                transactionDirection,
                request.getMinAmount(),
                request.getMaxAmount(),
                pageable
        );

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