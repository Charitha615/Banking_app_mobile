package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilebankingapplication.data.RegisterRequest;
import com.example.mobilebankingapplication.data.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullName, email, password,accNumber,branch;
    private Button registerButton;
    private TextView loginPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        accNumber = findViewById(R.id.accNumber);
        branch = findViewById(R.id.branch);
        password = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        loginPrompt = findViewById(R.id.loginPrompt);

        registerButton.setOnClickListener(v -> {
            String name = fullName.getText().toString();
            String userEmail = email.getText().toString();
            String useraccNumber = accNumber.getText().toString();
            String userbranch = branch.getText().toString();
            String userPassword = password.getText().toString();

            if (!name.isEmpty() && !userEmail.isEmpty() && !useraccNumber.isEmpty() && !userbranch.isEmpty() && !userPassword.isEmpty()) {
                registerUser(name, userEmail,useraccNumber,userbranch, userPassword);
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        loginPrompt.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginScreen.class));
        });
    }

    private void registerUser(String name, String email, String account_number, String branch, String password) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        RegisterRequest registerRequest = new RegisterRequest(name, email, password, "User", account_number, branch);

        Call<RegisterResponse> call = apiService.register(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                        finish(); // Go back to the login screen
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                t.printStackTrace(); // Print the stack trace
                Toast.makeText(RegisterActivity.this,  t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}