package com.example.app_2100.data_generators;

import androidx.annotation.NonNull;

import com.example.app_2100.CurrentUser;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.Post;
import com.example.app_2100.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserGenerator {

    private int nUsers;

    public UserGenerator(int nUsers){
        this.nUsers = nUsers;
    }
    public static void main(String[] args) {
        FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
        UserGenerator gen = new UserGenerator(20);
        Random rand = new Random();
        for (int i=0; i<gen.getNUsers(); i++) {
            // generate post
            Map<String, Object> user = new HashMap<>();
            user.put("firstName", "FIRSTNAME");
            user.put("lastName", "LASTNAME");
            uploadUser(user, db);
        }

    }

    private static void uploadUser(Map<String, Object> user, FirebaseFirestore db){
        CollectionReference postsCollection = db.collection("users");
        postsCollection.add(user)
                .addOnSuccessListener(documentReference -> {
                    // success
                })
                .addOnFailureListener(e -> {
                    // boohoo
                    System.out.println("ERR uploading post: "+ user);
                });
    }

    private int getNUsers() {
        return nUsers;
    }


}
