package com.example.app_2100;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Database {
    private static Database dbInstance = null;

    private Database(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }


    public static Database getInstance(){
        if (dbInstance == null){
            dbInstance = new Database();
        }

        return dbInstance;
    }
}
// private final ArrayList<String> listofstuff = Database.getInstance().getItemsList()