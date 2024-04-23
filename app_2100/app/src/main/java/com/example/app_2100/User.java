package com.example.app_2100;

public class User {
    private static User instance;
    public String firstName;
    public String lastName;
    public String userID;

    private User() {
        // Private constructor to prevent instantiation from outside
    }

    public static User getCurrent() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID() {
        return userID;
    }
}

