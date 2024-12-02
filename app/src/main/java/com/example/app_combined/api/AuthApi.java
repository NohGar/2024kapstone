package com.example.app_combined.api;

import com.example.app_combined.model.LoginRequest;
import com.example.app_combined.model.AuthResponse;
import com.example.app_combined.model.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("/register")
    public Call<AuthResponse> register(@Body SignupRequest loginRequest);
    @POST("/login")
    public Call<AuthResponse> login(@Body LoginRequest loginRequest);
}
