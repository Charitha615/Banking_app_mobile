package com.example.mobilebankingapplication;

public class Transaction {
    private String billType;
    private String accountNumber;
    private String amount;
    private String date;
    private String time;

    // Constructor
    public Transaction(String billType, String accountNumber, String amount, String date, String time) {
        this.billType = billType;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.date = date;
        this.time = time;
    }

    // Getters
    public String getBillType() {
        return billType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}