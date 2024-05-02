package com.example.app_2100;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseFirestoreConnection {
//    A singleton class to handle all FireStore operations
    private static FirebaseFirestoreConnection instance = null;
    private static FirebaseFirestore db = null;

    private FirebaseFirestoreConnection(){
       db = FirebaseFirestore.getInstance();
    }

    /**
     * Returns the instance of the FirebaseFirestoreConnection
     * @return
     */
    public static FirebaseFirestore getDb(){
        if(instance == null){
            instance = new FirebaseFirestoreConnection();
        }
        return db;
    }


}
