package com.mackena.Banking_Application_backend.models.enums;

public enum UserRole {
    USER("User"),
    ADMIN("Admin") ;

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
    public boolean isUser() {
        return this == USER;
    }
}
