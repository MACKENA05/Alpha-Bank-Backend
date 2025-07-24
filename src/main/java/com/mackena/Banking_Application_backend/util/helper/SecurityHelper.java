package com.mackena.Banking_Application_backend.util.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encodePin(String rawPin) {
        return passwordEncoder.encode(rawPin);
    }

    public boolean matchesPin(String rawPin, String encodedPin) {
        return passwordEncoder.matches(rawPin, encodedPin);
    }
}

