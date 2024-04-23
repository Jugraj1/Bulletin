package com.example.app_2100;

public class CurrentUser {
    private static CurrentUser instance;
    public String Name;
    public String Last;
    public String Id;

    private CurrentUser() {
        // Private constructor to prevent instantiation from outside
    }

    public static CurrentUser getCurrent() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }
}
