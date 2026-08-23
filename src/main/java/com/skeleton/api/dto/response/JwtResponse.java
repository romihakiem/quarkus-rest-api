package com.skeleton.api.dto.response;

public class JwtResponse {
    public String token;
    public String tokenType = "Bearer";
    public Long expiresIn; // seconds until expiry
    public UserResponse user;

    public JwtResponse() {
    }

    public JwtResponse(String token, Long expiresIn, UserResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
