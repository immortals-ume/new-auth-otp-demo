package com.demo.auth.authdemoproject.security.exception;

public class JwtNotFoundException extends Exception {
    public JwtNotFoundException(String message) {
        super(message);
    }
}
