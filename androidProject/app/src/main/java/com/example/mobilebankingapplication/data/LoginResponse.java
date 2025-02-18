package com.example.mobilebankingapplication.data;

public class LoginResponse {
    private String message;
    private String token;
    private User user;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private int id;
        private String name;
        private String email;
        private String user_type;
        private String account_number;
        private String branch;
        private String balance;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getUserType() {
            return user_type;
        }

        public String getAccountNumber() {
            return account_number;
        }

        public String getBranch() {
            return branch;
        }

        public String getBalance() {
            return balance;
        }
    }
}