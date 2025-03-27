package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Ticket {
    @Id
    private String token;
    private String username;
    private long expiryTime;

    public Ticket() {}

    public Ticket(String token, String username, long expiryTime) {
        this.token = token;
        this.username = username;
        this.expiryTime = expiryTime;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public long getExpiryTime() { return expiryTime; }
    public void setExpiryTime(long expiryTime) { this.expiryTime = expiryTime; }
}