package com.example.app_2100;

import com.google.firebase.firestore.FirebaseFirestore;

public class FireStoreClass {
//    A singleton class to handle all FireStore operations
    private static FireStoreClass instance = null;
    private static FirebaseFirestore db = null;

    private FireStoreClass (){
       db = FirebaseFirestore.getInstance();
    }

    /**
     * Returns the instance of the FireStoreClass
     * @return
     */
    public static FirebaseFirestore getDb(){
        if(instance == null){
            instance = new FireStoreClass();
        }
        return db;
    }


}
