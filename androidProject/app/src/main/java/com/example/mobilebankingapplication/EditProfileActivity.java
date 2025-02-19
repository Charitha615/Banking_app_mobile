package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);

        // Fetch and display current user details
        fetchUserDetails();

        // Set up save button click listener
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void fetchUserDetails() {
        int userId = SharedPrefManager.getInstance(this).getUserId();
        String token = SharedPrefManager.getInstance(this).getToken();
        String url = "http://10.0.2.2:8000/api/users/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Extract "user" object from the response
                        JSONObject user = response.getJSONObject("user");

                        // Get user details from the "user" object
                        etName.setText(user.getString("name"));
                        etEmail.setText(user.getString("email"));

                        // "phone" does not exist in API response, using "account_number" instead
                        etPhone.setText(user.getString("phone"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing user details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching user details", Toast.LENGTH_SHORT).show()
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


    private void saveProfile() {
        int userId = SharedPrefManager.getInstance(this).getUserId();
        String token = SharedPrefManager.getInstance(this).getToken();
        String url = "http://10.0.2.2:8000/api/users/" + userId + "/update";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", etName.getText().toString());
            jsonBody.put("email", etEmail.getText().toString());
            jsonBody.put("phone", etPhone.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Indicate success
                    startActivity(new Intent(EditProfileActivity.this, DashboardActivity.class));
                    finish(); // Close the activity
                },
                error -> Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
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
}