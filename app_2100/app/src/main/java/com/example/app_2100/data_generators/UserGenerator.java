package com.example.app_2100.data_generators;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app_2100.AuthCallback;
import com.example.app_2100.CreateAccount;
import com.example.app_2100.CurrentUser;
import com.example.app_2100.FirebaseAuthConnection;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.HomeFeed;
import com.example.app_2100.Post;
import com.example.app_2100.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserGenerator {

    private int nUsers;

    public UserGenerator(int nUsers){
        this.nUsers = nUsers;
    }

    /***
     * We use this code in main activity so that it runs properly
     * @param args
     */
    public static void main(String[] args) {
        FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
        UserGenerator gen = new UserGenerator(2);
        Random rand = new Random();
        for (int i=0; i<gen.getNUsers(); i++) {
            // generate post
            Map<String, Object> user = new HashMap<>();
            user.put("email", "FIRSTNAME"+i);
            user.put("firstName", "FIRSTNAME"+i);
            user.put("lastName", "LASTNAME"+i);
//            uploadUser(email, fName, lName);
        }
//        System.out.println("USER GEN");

    }

    public void uploadUser(String email, String fName, String lName){
        FirebaseAuthConnection.getInstance().createAccount(email, "pass123", fName, lName, createAccountCallback()); // give every "fake" user same pass
    }

    public int getNUsers() {
        return nUsers;
    }


    /**
     * Callback for createAccount specify what happens after account creation
     * Redirect to HomeFeed if successful
     * Display error message if unsuccessful
     * @return
     */
    private AuthCallback createAccountCallback (){
        return new AuthCallback() {
            @Override
            public void onAuthentication(boolean success) {

            }
        };
    }

}
