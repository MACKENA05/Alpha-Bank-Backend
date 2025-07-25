package com.mackena.Banking_Application_backend.models.enums;

public enum TransactionType {
    WITHDRAW("Withdraw"),
    DEPOSIT("Deposit"),
    TRANSFER("Transfer");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}