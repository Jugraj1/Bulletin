package com.example.app_2100;

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
     * Callback interface for authentication
     */
    public interface AuthCallback {
        void onAuthentication(boolean success);
    }
}
