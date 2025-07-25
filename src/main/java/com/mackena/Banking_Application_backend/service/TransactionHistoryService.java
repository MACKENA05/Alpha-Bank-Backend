package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.TransactionHistoryRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionHistoryResponse;
import com.mackena.Banking_Application_backend.models.entity.User;

public interface TransactionHistoryService {
    TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request, User currentUser);
}
