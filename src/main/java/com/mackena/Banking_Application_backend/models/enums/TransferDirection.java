package com.mackena.Banking_Application_backend.models.enums;

public enum TransferDirection {
    DEBIT("debit"),
    CREDIT("credit");

    private final String value;

    TransferDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
