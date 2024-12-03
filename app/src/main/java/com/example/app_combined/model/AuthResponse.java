package com.example.app_combined.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse { // JWT 토큰 발급
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("tokenType")
    private String tokenType;

    public AuthResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
