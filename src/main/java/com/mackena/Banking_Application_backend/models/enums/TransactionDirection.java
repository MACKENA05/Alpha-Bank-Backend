package com.mackena.Banking_Application_backend.models.enums;

public enum TransactionDirection {
    DEBIT("debit"),
    CREDIT("credit");

    private final String value;

    TransactionDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
