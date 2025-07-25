package com.mackena.Banking_Application_backend.util.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AccountNumberGenerator {

    private static final String PREFIX = "ACC";
    private static final SecureRandom random = new SecureRandom();

    public String generateAccountNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", random.nextInt(10000));
        return PREFIX + datePart + randomPart;
    }

}