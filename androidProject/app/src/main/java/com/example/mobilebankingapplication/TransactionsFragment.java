package com.example.mobilebankingapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionsFragment extends Fragment {
    private RecyclerView transactionsRecyclerView;
    private TransactionsAdapter transactionsAdapter;
    private List<Transaction> transactions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // Initialize RecyclerView
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data and adapter
        transactions = new ArrayList<>();
        transactionsAdapter = new TransactionsAdapter(transactions);
        transactionsRecyclerView.setAdapter(transactionsAdapter);

        // Fetch data (e.g., from an API or local database)
        fetchTransactions();

        return view;
    }

    private void fetchTransactions() {
        // Get userId and token from SharedPrefManager
        int userId = SharedPrefManager.getInstance(getContext()).getUserId();
        String token = SharedPrefManager.getInstance(getContext()).getToken();

        // Construct the API URL
        String url = "http://10.0.2.2:8000/api/paybills/user/" + userId;

        // Log the URL and token for debugging
        Log.d("API_REQUEST", "URL: " + url);
        Log.d("API_REQUEST", "Token: " + token);

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Create a JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, null,
                response -> {
                    try {
                        // Log the raw response for debugging
                        Log.d("API_RESPONSE", "Response: " + response.toString());

                        // Parse the response
                        JSONArray transactionsArray = response.getJSONArray("transactions");
                        for (int i = 0; i < transactionsArray.length(); i++) {
                            JSONObject transactionJson = transactionsArray.getJSONObject(i);
                            String billType = transactionJson.getString("bill_type");
                            String accountNumber = transactionJson.getString("account_number");
                            String amount = transactionJson.getString("amount");
                            String date = transactionJson.getString("date");
                            String time = transactionJson.getString("time");

                            // Add the transaction to the list
                            transactions.add(new Transaction(billType, accountNumber, amount, date, time));
                        }

                        // Notify the adapter that data has changed
                        transactionsAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("API_ERROR", "Error parsing JSON: " + e.getMessage());
                    }
                },
                error -> {
                    // Log the error details
                    Log.e("API_ERROR", "Error fetching transactions: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("API_ERROR", "Status code: " + error.networkResponse.statusCode);
                        Log.e("API_ERROR", "Response data: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(getContext(), "Failed to fetch transactions", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Add the Bearer token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the queue
        queue.add(jsonObjectRequest);
    }
}