package com.mackena.Banking_Application_backend.models.enums;


public enum AccountType {
    SAVINGS("Savings Account"),
    CHECKING("Checking Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
