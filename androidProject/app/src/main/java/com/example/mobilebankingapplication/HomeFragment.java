package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    private TextView accountBalanceTextView;

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

        // Update account balance
        updateAccountBalance();

        return view;
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