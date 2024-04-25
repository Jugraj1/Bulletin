package com.example.app_2100;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthClass {
    // A singleton class to handle all Firebase operations
    private static FirebaseAuthClass instance = null;
    private static FirebaseAuth mAuth;

    private FirebaseAuthClass(){
        mAuth = FirebaseAuth.getInstance();
    }


//    DELETE THIS METHOD IF IT IS NOT NEEDED!!!!!!
    /**
     * Returns the instance of the FirebaseAuthClass
     * @return
     */
    public static FirebaseAuthClass getInstance(){
        if (instance == null){
            instance = new FirebaseAuthClass();
        }
        return instance;
    }

    /**
     * Returns the FirebaseAuth instance
     * @return
     */
    public static FirebaseAuth getAuth(){
        if (instance == null){
            instance = new FirebaseAuthClass();
        }
        return mAuth;
    }

    /**
     * Returns the current user
     * @return
     */
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuthClass.getAuth().getCurrentUser();
    }
}
