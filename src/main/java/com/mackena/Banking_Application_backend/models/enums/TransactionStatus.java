package com.mackena.Banking_Application_backend.models.enums;

public enum TransactionStatus {
    PENDING("Transaction is being processed"),
    COMPLETED("Transaction completed successfully"),
    FAILED("Transaction failed"),
    CANCELLED("Transaction was cancelled");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
