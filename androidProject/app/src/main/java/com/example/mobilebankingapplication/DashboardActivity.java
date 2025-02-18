package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // Load user details from SharedPreferences
        loadUserDetails();

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Load the default fragment (e.g., HomeFragment)
        loadFragment(new HomeFragment());
    }

    private void loadUserDetails() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        tvUserName.setText("Welcome, " + sharedPrefManager.getUserName() + "!");
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