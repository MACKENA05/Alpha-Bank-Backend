package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.TransactionHistoryRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionDetailResponse;
import com.mackena.Banking_Application_backend.dtos.response.TransactionHistoryResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionHistoryService {

    @Transactional(readOnly = true)
    TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request, User currentUser);

    @Transactional(readOnly = true)
    TransactionDetailResponse getTransactionById(Long transactionId, User currentUser);

    @Transactional(readOnly = true)
    TransactionDetailResponse getTransactionByReference(String referenceNumber, User currentUser);

    @Transactional(readOnly = true)
    TransactionHistoryResponse getAllTransactionsForAdmin(TransactionHistoryRequest request);

    @Transactional(readOnly = true)
    TransactionHistoryResponse getAccountTransactions(Long accountId, TransactionHistoryRequest request);

    TransactionHistoryResponse getUserTransactionsForAdmin(TransactionHistoryRequest request);

}
