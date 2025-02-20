package com.example.mobilebankingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvAccountNumber, tvBalance;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvBalance = findViewById(R.id.tvBalance);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Fetch and update user details from the API
        fetchUserDetails();

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Load the default fragment (e.g., HomeFragment)
        loadFragment(new HomeFragment());
    }

    private void fetchUserDetails() {
        int userId = SharedPrefManager.getInstance(this).getUserId();
        String token = SharedPrefManager.getInstance(this).getToken();
        String url = "http://10.0.2.2:8000/api/users/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Parse the response
                        JSONObject userJson = response.getJSONObject("user");
                        String name = userJson.getString("name");
                        String email = userJson.getString("email");
                        String accountNumber = userJson.getString("account_number");
                        String balance = userJson.getString("balance");

                        // Update SharedPrefManager
                        SharedPrefManager.getInstance(this).saveUserDetails(
                                token,
                                userId,
                                name,
                                email,
                                userJson.getString("user_type"),
                                accountNumber,
                                userJson.getString("branch"),
                                balance
                        );

                        // Update the UI with the latest details
                        loadUserDetails();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("API_ERROR", "Error parsing user details: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("API_ERROR", "Error fetching user details: " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @SuppressLint("SetTextI18n")
    private void loadUserDetails() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        tvUserName.setText("Welcome, " + sharedPrefManager.getUserName() + "!  " + sharedPrefManager.getUserId());
        tvUserEmail.setText(sharedPrefManager.getUserEmail());
        tvAccountNumber.setText("Account Number: " + sharedPrefManager.getAccountNumber());
        tvBalance.setText("Balance: $" + sharedPrefManager.getBalance());
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_transactions) {
            selectedFragment = new TransactionsFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}