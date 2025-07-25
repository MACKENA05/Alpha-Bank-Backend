package com.mackena.Banking_Application_backend.models.enums;

public enum TransactionDirection {
    DEBIT("Money going out"),
    CREDIT("Money Going In");

    private final String value;

    TransactionDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
