package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.TransferRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransferResponse;
import com.mackena.Banking_Application_backend.models.entity.User;

public interface TransferService {

    TransferResponse transferMoney(TransferRequest request, User currentUser);

}
