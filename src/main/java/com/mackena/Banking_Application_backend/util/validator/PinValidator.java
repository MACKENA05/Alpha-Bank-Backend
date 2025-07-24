package com.mackena.Banking_Application_backend.util.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PinValidator {

    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4}$");

    public boolean isValidPin(String pin) {
        return pin != null && PIN_PATTERN.matcher(pin).matches();
    }

    public boolean isPinSecure(String pin) {
        if (!isValidPin(pin)) return false;

        // Check for obvious patterns
        if (pin.equals("0000") || pin.equals("1234") || pin.equals("1111") ||
                pin.equals("2222") || pin.equals("3333") || pin.equals("4444")) {
            return false;
        }

        // Check for sequential numbers
        if (isSequential(pin)) {
            return false;
        }

        return true;
    }

    private boolean isSequential(String pin) {
        for (int i = 0; i < pin.length() - 1; i++) {
            int current = Character.getNumericValue(pin.charAt(i));
            int next = Character.getNumericValue(pin.charAt(i + 1));
            if (Math.abs(current - next) != 1) {
                return false;
            }
        }
        return true;
    }
}
