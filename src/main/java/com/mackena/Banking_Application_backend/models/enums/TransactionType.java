package com.mackena.Banking_Application_backend.models.enums;

public enum TransactionType {

    TRANSFER_OUT("Transfer Out"),
    TRANSFER_IN("Transfer In");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}