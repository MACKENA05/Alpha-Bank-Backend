package com.mackena.Banking_Application_backend.util.formatter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Component
public class CurrencyFormatter {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final String CURRENCY_PREFIX = "KES ";

    public String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return CURRENCY_PREFIX + "0.00";
        }
        return CURRENCY_PREFIX + CURRENCY_FORMAT.format(amount);
    }

    public String formatAmountWithSign(BigDecimal amount, boolean isDebit) {
        String formattedAmount = formatAmount(amount);
        return (isDebit ? "-" : "+") + formattedAmount;
    }
}
