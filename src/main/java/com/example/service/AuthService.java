package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // Simple in-memory credential check (replace with database later)
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "secret123";

    public boolean authenticate(String username, char[] password) {
        if (username == null || password == null) {
            return false;
        }
        String passwordStr = new String(password);
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(passwordStr);
    }
}