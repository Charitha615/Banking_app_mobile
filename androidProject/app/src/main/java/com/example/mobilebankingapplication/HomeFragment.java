package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView accountBalanceTextView;
    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        accountBalanceTextView = view.findViewById(R.id.accountBalance);

        // Set up Quick Actions click listeners
        view.findViewById(R.id.payBillsCard).setOnClickListener(v -> openPayBillsScreen());
        view.findViewById(R.id.transferFundsCard).setOnClickListener(v -> openTransferFundsScreen());
        view.findViewById(R.id.creditCardCard).setOnClickListener(v -> openCreditCardScreen());

        // Initialize Notifications RecyclerView
        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsAdapter = new NotificationsAdapter(notifications);
        notificationsRecyclerView.setAdapter(notificationsAdapter);

        // Update account balance
        updateAccountBalance();
        fetchNotifications();
        return view;
    }

    private void fetchNotifications() {
        int userId = SharedPrefManager.getInstance(requireContext()).getUserId();
        String token = SharedPrefManager.getInstance(requireContext()).getToken();
        String url = "http://10.0.2.2:8000/api/users/" + userId + "/notifications";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray notificationsArray = response.getJSONArray("notifications");
                        notifications.clear();

                        for (int i = 0; i < notificationsArray.length(); i++) {
                            JSONObject notificationJson = notificationsArray.getJSONObject(i);
                            notifications.add(new Notification(
                                    notificationJson.getString("title"),
                                    notificationJson.getString("description"),
                                    notificationJson.getString("created_at")
                            ));
                        }
                        notificationsAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(requireContext(), "No notifications", Toast.LENGTH_SHORT).show()
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

    private void updateAccountBalance() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        // Fetch balance from SharedPrefManager
        double balance = Double.parseDouble(sharedPrefManager.getBalance());

        // Update the TextView
        accountBalanceTextView.setText(String.format("$%.2f", balance));
    }

    private void openPayBillsScreen() {
        // Navigate to Pay Bills Activity
        Intent intent = new Intent(requireContext(), PayBills.class);
        startActivity(intent);
    }

    private void openTransferFundsScreen() {
        // Navigate to Transfer Funds Activity
        Intent intent = new Intent(requireContext(), TranferFunds.class);
        startActivity(intent);
    }

    private void openCreditCardScreen() {
        // Navigate to Credit Card Activity
        Intent intent = new Intent(requireContext(), CreditCard.class);
        startActivity(intent);
    }
}