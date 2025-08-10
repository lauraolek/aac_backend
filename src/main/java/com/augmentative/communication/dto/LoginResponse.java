package com.augmentative.communication.dto;

public class LoginResponse {
    private String token; // Renamed jwt to token
    private Long userId;

    public LoginResponse(String token, Long userId) { // Updated constructor
        this.token = token;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() { // Renamed getJwt to getToken
        return token;
    }

    public void setToken(String token) { // Renamed setJwt to setToken
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}