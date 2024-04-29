package com.example.a61;

public class Task {
    private long id;
    private long userId;
    private String title;
    private String description;

    // Default constructor
    public Task() {
    }

    // Constructor with parameters to initialize the task
    public Task(long userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
    }

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
