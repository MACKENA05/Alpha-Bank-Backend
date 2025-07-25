package com.mackena.Banking_Application_backend.util.validator;


import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class AccountValidator {

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^ACC\\d{8}\\d{4}$");

    public boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }

    public boolean isValidTransactionAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
                amount.compareTo(new BigDecimal("1000000")) <= 0; // Max 1M
    }

    public boolean isValidAccountType(String accountType) {
        return accountType != null &&
                (accountType.equals("SAVINGS") || accountType.equals("CURRENT"));
    }
}