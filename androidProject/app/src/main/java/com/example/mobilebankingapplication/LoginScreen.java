package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilebankingapplication.data.LoginRequest;
import com.example.mobilebankingapplication.data.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton;
    private TextView registerPrompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerPrompt = findViewById(R.id.registerPrompt);

        loginButton.setOnClickListener(v -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
                loginUser(userEmail, userPassword);
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        registerPrompt.setOnClickListener(v -> {
            startActivity(new Intent(LoginScreen.this, RegisterActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    LoginResponse.User user = loginResponse.getUser();

                    // Save user details using SharedPreferences
                    SharedPrefManager.getInstance(LoginScreen.this).saveUserDetails(
                            token,
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getUserType(),
                            user.getAccountNumber(),
                            user.getBranch(),
                            user.getBalance()
                    );

                    // Or save user details using Singleton
//                    UserSession.getInstance().setUserDetails(
//                            token,
//                            user.getId(),
//                            user.getName(),
//                            user.getEmail(),
//                            user.getUserType(),
//                            user.getAccountNumber(),
//                            user.getBranch(),
//                            user.getBalance()
//                    );

                    Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginScreen.this, MainActivity.class));
                    finish(); // Close the login screen
                } else {
                    Toast.makeText(LoginScreen.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginScreen.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}