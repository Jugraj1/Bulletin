package com.example.app_2100.callbacks;

public interface FirestoreCallback {
    void onUserLoaded(String firstName, String lastName, String username, String pfpLink);
}
