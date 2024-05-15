package com.example.app_2100;

import android.util.Log;

import com.example.app_2100.callbacks.AuthCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthConnection {
    // A singleton class to handle all Firebase operations
    private static FirebaseAuthConnection instance = null;
    private static FirebaseAuth mAuth;
    private FirebaseAuthConnection(){
        mAuth = FirebaseAuth.getInstance();
    }

    private static String TAG = "FirebaseAuthConnection";


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
     * @param username
     * @param callback
     */
    public void createAccount(String email, String password, String firstName, String lastName, boolean defaultPicture, String username, AuthCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // sign in success
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        if (currentUserID != null){
                            updateAccount(currentUserID, firstName, lastName, defaultPicture, username);
                        } else {
                            Log.d(TAG, "Currently signed in user is null :(");
                        }

                        callback.onAuthentication(true);

                    } else {
                        callback.onAuthentication(false);
                    }
                });
    }

    /**
     * Update the user with the given first name and last name and
     * Add an empty array of Strings called following
     * @param userId
     * @param firstName
     * @param lastName
     * @param username
     */
    private void updateAccount(String userId, String firstName, String lastName, boolean defaultPicture, String username){
        // update the user with the first name and last name
        String pfpStorageLink = "gs://app-f4755.appspot.com/pfp/" + userId + ".jpg";


        Map<String, Object> newUser = new HashMap<>();
        newUser.put("firstName", firstName);
        newUser.put("lastName", lastName);
        newUser.put("following", Collections.emptyList());
        newUser.put("username", username);

//        Only put profile storage link if it is not default picture
        if(!defaultPicture) {
            newUser.put("pfpStorageLink", pfpStorageLink);
        }

        FirebaseFirestoreConnection.getDb()
                .collection("users")
                .document(userId)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    // update success
                    Log.d("FirebaseAuthConnection", "User creation successfully");
                }).addOnFailureListener(e -> {
                    // update failed
                    Log.d("FirebaseAuthConnection", "User creation failed: "+e);
                });
    }

}
