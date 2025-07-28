package com.mackena.Banking_Application_backend.util.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String encodePin(String pin) {
        return passwordEncoder.encode(pin);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean matchesPin(String rawPin, String encodedPin) {
        return passwordEncoder.matches(rawPin, encodedPin);
    }
};

