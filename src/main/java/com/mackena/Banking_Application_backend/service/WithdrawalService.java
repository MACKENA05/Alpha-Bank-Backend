package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.WithdrawalRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface WithdrawalService {

    @Transactional
    TransactionResponse processWithdrawal (WithdrawalRequest request, User currentUser);
}
