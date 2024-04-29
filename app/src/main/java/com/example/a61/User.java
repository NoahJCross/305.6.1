package com.example.a61;

public class User {
    private static User instance;
    private long id;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;

    // Singleton pattern: static method to get instance
    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    // Singleton pattern: static method to get instance with parameters
    public static User getInstance(String username, String email, String password, String phoneNumber) {
        if (instance == null) {
            instance = new User();
            instance.username = username;
            instance.email = email;
            instance.password = password;
            instance.phoneNumber = phoneNumber;
        }
        return instance;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
