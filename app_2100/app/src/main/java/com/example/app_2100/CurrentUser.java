package com.example.app_2100;

public class CurrentUser {
    private static CurrentUser instance;
    public String firstName;
    public String lastName;
    public String userID;

    private CurrentUser() {
        // Private constructor to prevent instantiation from outside
    }

    public static CurrentUser getCurrent() {
        if (instance == null) {
            instance = new CurrentUser();
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

