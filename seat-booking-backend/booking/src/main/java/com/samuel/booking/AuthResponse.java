package com.samuel.booking;

// This class is the "box" that holds your Token and User ID
public class AuthResponse {
    private String token;
    private Long id;

    public AuthResponse(String token, Long id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
}
