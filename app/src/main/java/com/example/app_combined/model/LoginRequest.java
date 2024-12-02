package com.example.app_combined.model;

import com.google.gson.annotations.SerializedName;


public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
