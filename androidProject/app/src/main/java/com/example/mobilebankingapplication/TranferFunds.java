package com.example.mobilebankingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class TranferFunds extends AppCompatActivity {

    private TextView fromAccountText;
    private EditText toAccountInput, amountInput;
    private Button transferButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tranfer_funds);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        fromAccountText = findViewById(R.id.fromAccountText);
        toAccountInput = findViewById(R.id.toAccountInput);
        amountInput = findViewById(R.id.amountInput);
        transferButton = findViewById(R.id.transferButton);

        // Set From Account text (retrieved from SharedPrefManager)
        String accountNumber = SharedPrefManager.getInstance(this).getAccountNumber();
        fromAccountText.setText("From "+accountNumber);

        // Set up transfer button click listener
        transferButton.setOnClickListener(v -> initiateTransfer());
    }

    private void initiateTransfer() {
        // Get user inputs
        String toAccount = toAccountInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();

        // Validate inputs
        if (toAccount.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user ID and token from SharedPrefManager
        int userId = SharedPrefManager.getInstance(this).getUserId();
        String token = SharedPrefManager.getInstance(this).getToken();

        // Create the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_id", userId);
            requestBody.put("account_number", toAccount); // Recipient's account number
            requestBody.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            return;
        }

        // API URL
        String url = "http://10.0.2.2:8000/api/transfer/create";

        // Create the request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        // Parse the response
                        String message = response.getString("message");
                        JSONObject data = response.getJSONObject("data");

                        // Display success message
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TranferFunds.this, DashboardActivity.class));
                        finish(); // Close the login screen

                        // Log the transfer details (optional)
                        String transferDetails = "Transfer ID: " + data.getInt("id") +
                                "\nAmount: " + data.getString("amount") +
                                "\nDate: " + data.getString("date") +
                                "\nTime: " + data.getString("time");
                        System.out.println(transferDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Transfer failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Add the request to the Volley queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}