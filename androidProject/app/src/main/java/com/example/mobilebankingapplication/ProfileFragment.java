package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private Button btnEditProfile, btnDeactivateProfile, btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserPhone = view.findViewById(R.id.tvUserPhone);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnDeactivateProfile = view.findViewById(R.id.btnDeactivateProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Fetch and display user details
        fetchUserDetails();

        // Set up button click listeners
        btnEditProfile.setOnClickListener(v -> editProfile());
        btnDeactivateProfile.setOnClickListener(v -> showDeactivateConfirmationDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void fetchUserDetails() {
        int userId = SharedPrefManager.getInstance(requireContext()).getUserId();
        String token = SharedPrefManager.getInstance(requireContext()).getToken();
        String url = "http://10.0.2.2:8000/api/users/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Extract "user" object from the response
                        JSONObject user = response.getJSONObject("user");

                        // Get user details from the "user" object
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String phone = user.getString("phone");

                        // Update UI with user details
                        tvUserName.setText(name);
                        tvUserEmail.setText(email);
                        tvUserPhone.setText(phone); // Consider renaming this variable if needed
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing user details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error fetching user details", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void editProfile() {
        // Navigate to Edit Profile Activity
        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void showDeactivateConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Deactivate Profile")
                .setMessage("Are you sure you want to deactivate your profile? This action cannot be undone.")
                .setPositiveButton("Confirm", (dialog, which) -> deactivateProfile())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deactivateProfile() {
        int userId = SharedPrefManager.getInstance(requireContext()).getUserId();
        String token = SharedPrefManager.getInstance(requireContext()).getToken();
        String url = "http://10.0.2.2:8000/api/users/" + userId + "/deactivate";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(requireContext(), "Profile deactivated successfully", Toast.LENGTH_SHORT).show();
                    logout(); // Logout after deactivation
                },
                error -> Toast.makeText(requireContext(), "Error deactivating profile", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void logout() {
        // Clear user session
        SharedPrefManager.getInstance(requireContext()).clear();

        // Navigate to Login Activity
        Intent intent = new Intent(requireContext(), LoginScreen.class);
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }
}