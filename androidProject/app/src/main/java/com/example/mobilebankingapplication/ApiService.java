package com.example.mobilebankingapplication;

import com.example.mobilebankingapplication.data.LoginRequest;
import com.example.mobilebankingapplication.data.LoginResponse;
import com.example.mobilebankingapplication.data.RegisterRequest;
import com.example.mobilebankingapplication.data.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}