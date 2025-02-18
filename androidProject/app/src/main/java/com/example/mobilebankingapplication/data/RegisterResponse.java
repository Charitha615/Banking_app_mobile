package com.example.mobilebankingapplication.data;

public class RegisterResponse {
    private boolean success;
    private String message;
    private UserData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }

    public static class UserData {
        private User user;
        private String token;

        public User getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }

        public static class User {
            private int id;
            private String name;
            private String email;
            private String user_type;

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
        }
    }
}