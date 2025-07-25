package com.mackena.Banking_Application_backend.models.enums;

public enum TransactionTypes {
    DEBIT("debit"),
    CREDIT("credit");

    private final String value;

    TransactionTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
