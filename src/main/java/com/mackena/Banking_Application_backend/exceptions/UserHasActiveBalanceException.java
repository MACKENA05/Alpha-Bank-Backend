package com.mackena.Banking_Application_backend.exceptions;

import java.math.BigDecimal;

public class UserHasActiveBalanceException extends RuntimeException {

    private final BigDecimal totalBalance;

    public UserHasActiveBalanceException(String message, BigDecimal totalBalance) {
        super(message);
        this.totalBalance = totalBalance;
    }

    // Optionally include a default constructor
    public UserHasActiveBalanceException(String message) {
        super(message);
        this.totalBalance = BigDecimal.ZERO; // or `null`
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
}
