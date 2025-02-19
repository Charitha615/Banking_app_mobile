package com.example.mobilebankingapplication.data;

public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String user_type;
    private String account_number;
    private String branch;

    public RegisterRequest(String name, String email,String phone, String password, String user_type, String account_number, String branch) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.user_type = user_type;
        this.account_number = account_number;
        this.branch = branch;
    }
}