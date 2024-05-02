package com.example.app_2100;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthConnection {
    // A singleton class to handle all Firebase operations
    private static FirebaseAuthConnection instance = null;
    private static FirebaseAuth mAuth;
    private FirebaseAuthConnection(){
        mAuth = FirebaseAuth.getInstance();
    }


//    DELETE THIS METHOD IF IT IS NOT NEEDED!!!!!!
    /**
     * Returns the instance of the FirebaseAuthConnection
     * @return
     */
    public static FirebaseAuthConnection getInstance(){
        if (instance == null){
            instance = new FirebaseAuthConnection();
        }
        return instance;
    }

    /**
     * Returns the FirebaseAuth instance
     * @return
     */
    public static FirebaseAuth getAuth(){
        if (instance == null){
            instance = new FirebaseAuthConnection();
        }
        return mAuth;
    }

    /**
     * Returns the current user
     * @return
     */
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuthConnection.getAuth().getCurrentUser();
    }


    /***
     *
     * @param email
     * @param password
     * @param callback
     *  usage: FirebaseAuthConnection.getInstance().validateCredentials(email, password, new FirebaseAuthConnection.AuthCallback() {
     *     @Override
     *     public void onAuthentication(boolean success) {
     *         if (success) {
     *             // authentication success
     *         } else {
     *             // authentication failed
     *         }
     *     }
     * });
     */
    public void signIn (String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onAuthentication(true);
                    } else {
                        callback.onAuthentication(false);
                    }
                });
    }


    /**
     * Create a new account with the given email and password
     * @param email
     * @param password
     * @param callback
     */
    public void createAccount(String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onAuthentication(true);
                    } else {
                        callback.onAuthentication(false);
                    }
                });
    }

    /**
     * Create a new account with the given email, password, first name, and last name
     * Since we are using Firebase, we can only create an account with email and password
     * The user will be updated after creating account with the first name and last name
     * @param email
     * @param password
     * @param firstName
     * @param lastName
     * @param callback
     */
    public void createAccount(String email, String password, String firstName, String lastName, AuthCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // sign in success
                        String currentUser = CurrentUser.getCurrent().getUserID();
                        updateAccount(currentUser, firstName, lastName);
                        callback.onAuthentication(true);

                    } else {
                        callback.onAuthentication(false);
                        return;
                    }
                });
    }

    /**
     * Update the user with the given first name and last name
     * @param userId
     * @param firstName
     * @param lastName
     */
    private void updateAccount(String userId, String firstName, String lastName){
        // update the user with the first name and last name
        FirebaseFirestoreConnection.getDb().collection("users").document(userId)
                .update("firstName", firstName, "lastName", lastName)
                .addOnSuccessListener(aVoid -> {
                    // update success
                    Log.d("FirebaseAuthConnection", "User updated successfully");
                }).addOnFailureListener(e -> {
                    // update failed
                    Log.d("FirebaseAuthConnection", "User update failed");
                });
    }

}
