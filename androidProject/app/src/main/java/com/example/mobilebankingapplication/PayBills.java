package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class PayBills extends AppCompatActivity {

    private AutoCompleteTextView billTypeDropdown;
    private LinearLayout electricityAccountNumberLayout;
    private LinearLayout mobileNumberLayout;
    private EditText electricityAccountNumberInput;
    private EditText mobileNumberInput;
    private EditText amountInput;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay_bills);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        billTypeDropdown = findViewById(R.id.billTypeDropdown);
        electricityAccountNumberLayout = findViewById(R.id.electricityAccountNumberLayout);
        mobileNumberLayout = findViewById(R.id.mobileNumberLayout);
        electricityAccountNumberInput = findViewById(R.id.electricityAccountNumberInput);
        mobileNumberInput = findViewById(R.id.mobileNumberInput);
        amountInput = findViewById(R.id.amountInput);
        payButton = findViewById(R.id.payButton);

        // Set up the dropdown adapter
        String[] billTypes = {"Electricity", "Water", "Internet", "Gas", "TV Subscription"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, billTypes);
        billTypeDropdown.setAdapter(adapter);
        billTypeDropdown.setThreshold(1); // Ensure dropdown appears with minimal input


        // Handle dropdown item selection
        billTypeDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBillType = (String) parent.getItemAtPosition(position);
                updateInputFields(selectedBillType);
            }
        });

        // Handle pay button click
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current balance from SharedPrefManager
                double currentBalance = Double.parseDouble(SharedPrefManager.getInstance(PayBills.this).getBalance());
                double paymentAmount = Double.parseDouble(amountInput.getText().toString());

                // Check if the balance is sufficient
                if (currentBalance < paymentAmount) {
                    // If balance is insufficient, send a notification with the current date and time
                    String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    String notificationMessage = "Insufficient balance for payment. Current balance: " + currentBalance + ". Required: " + paymentAmount + ". Date: " + currentDateTime;

                    // Display a toast notification
                    Toast.makeText(PayBills.this, notificationMessage, Toast.LENGTH_LONG).show();

                    // Optionally, log the notification message
                    Log.d("INSUFFICIENT_BALANCE", notificationMessage);

                    // Exit the onClick method to prevent further processing
                    return;
                }

                // If balance is sufficient, proceed with the payment process
                String selectedBillType = billTypeDropdown.getText().toString();
                String accountNumber = selectedBillType.equals("Electricity") || selectedBillType.equals("Water") || selectedBillType.equals("Gas")
                        ? electricityAccountNumberInput.getText().toString()
                        : mobileNumberInput.getText().toString();
                String amount = amountInput.getText().toString();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); // Dynamically set the current date
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()); // Dynamically set the current time

                // Create the JSON object for the request body
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("user_id", SharedPrefManager.getInstance(PayBills.this).getUserId());
                    jsonBody.put("bill_type", selectedBillType);
                    jsonBody.put("account_number", accountNumber);
                    jsonBody.put("amount", amount);
                    jsonBody.put("date", date);
                    jsonBody.put("time", time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Instantiate the RequestQueue
                RequestQueue queue = Volley.newRequestQueue(PayBills.this);
                String url = "http://10.0.2.2:8000/api/paybills/create"; // Replace with your actual API URL

                // Request a JSON response from the provided URL
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        response -> {
                            // Handle the response
                            Toast.makeText(PayBills.this, "Payment successful", Toast.LENGTH_SHORT).show();
                            Log.d("API_RESPONSE", response.toString());
                            startActivity(new Intent(PayBills.this, DashboardActivity.class));
                            finish(); // Close the login screen
                        },
                        error -> {
                            // Handle the error
                            Toast.makeText(PayBills.this, "Payment failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("API_ERROR", error.getMessage());
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(PayBills.this).getToken());
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                // Add the request to the RequestQueue
                queue.add(jsonObjectRequest);
            }
        });
    }

    private void updateInputFields(String selectedBillType) {
        // Hide all input fields first
        electricityAccountNumberLayout.setVisibility(View.GONE);
        mobileNumberLayout.setVisibility(View.GONE);

        // Show the appropriate input field based on the selected bill type
        switch (selectedBillType) {
            case "Electricity":
                electricityAccountNumberLayout.setVisibility(View.VISIBLE);
                break;

            case "Water":
                electricityAccountNumberLayout.setVisibility(View.VISIBLE);
                break;

            case "Gas":
                electricityAccountNumberLayout.setVisibility(View.VISIBLE);
                break;

            case "Internet":
                mobileNumberLayout.setVisibility(View.VISIBLE);
                break;

            case "TV Subscription":
                mobileNumberLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}