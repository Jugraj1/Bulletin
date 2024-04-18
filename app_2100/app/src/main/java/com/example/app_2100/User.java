package com.example.app_2100;

public class User {
    private static User instance;
    public String Name;
    public String Last;
    public String Id;

    private User() {
        // Private constructor to prevent instantiation from outside
    }

    public static User getCurrent() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public static void follow(User usr) {

    }
}
