package com.example.demo.dto;

// POST /api/auth/login → LoginResponse
public class LoginResponse {
    public boolean success;
    public String message;
    public String token;
    public Long userId;
    public String username;

    public LoginResponse(boolean success, String message, String token, Long userId, String username) {
        this.success  = success;
        this.message  = message;
        this.token    = token;
        this.userId   = userId;
        this.username = username;
    }
}