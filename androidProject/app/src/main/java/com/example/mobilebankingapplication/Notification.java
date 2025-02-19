package com.example.mobilebankingapplication;

public class Notification {
    private String title;
    private String description;
    private String createdAt;

    public Notification(String title, String description, String createdAt) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}