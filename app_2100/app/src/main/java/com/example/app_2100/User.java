package com.example.app_2100;

public class User {
    private String firstName;
    private String lastName;
    private String userID;

    private String TAG = "User";

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
