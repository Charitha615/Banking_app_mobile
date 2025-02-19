package com.example.mobilebankingapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
                // Handle payment logic here
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