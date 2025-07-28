package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.DepositRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionResponse;
import com.mackena.Banking_Application_backend.models.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface DepositService {

    @Transactional
    TransactionResponse processDeposit(DepositRequest request, User adminUser);
}
