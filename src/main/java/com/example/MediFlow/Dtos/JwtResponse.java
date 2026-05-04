package com.example.MediFlow.Dtos;


import com.example.MediFlow.Dtos.user_dto.UserResponse;

public class JwtResponse {

    private UserResponse user;
    private String jwtToken;
    private String avatarUrl; // Add this field

    public JwtResponse(UserResponse user, String jwtToken, String avatarUrl) {
        this.user = user;
        this.jwtToken = jwtToken;
        this.avatarUrl = avatarUrl;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
