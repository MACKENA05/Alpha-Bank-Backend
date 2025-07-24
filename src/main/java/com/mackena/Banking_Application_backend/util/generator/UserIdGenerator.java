package com.mackena.Banking_Application_backend.util.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserIdGenerator {

    private static final SecureRandom random = new SecureRandom();

    public String generateCustomerId() {
        return "CUST" + String.format("%08d", random.nextInt(100000000));

    }
};