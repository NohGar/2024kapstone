package com.example.app_combined.api;


import com.example.app_combined.model.LoginRequest;
import com.example.app_combined.model.AuthResponse;
import com.example.app_combined.model.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {

    @FormUrlEncoded
    @POST("/register")
    public Call<AuthResponse> register(@Body SignupRequest signupRequest);
    @POST("/login")
    public Call<AuthResponse> login(@Body LoginRequest loginRequest);
}
