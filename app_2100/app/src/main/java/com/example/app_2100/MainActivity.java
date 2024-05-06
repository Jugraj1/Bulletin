package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.app_2100.data_generators.PostGenerator;
import com.example.app_2100.data_generators.UserGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuthConnection.getCurrentUser();
        FirebaseAuth.getInstance().signOut();
        generateUsers();

        if(currentUser != null) { // user is logged in already
            startActivity(new Intent(MainActivity.this, HomeFeed.class));
//            Log.d(TAG, "logged in already");
        } else { // no currently logged in user
            startActivity(new Intent(MainActivity.this, Login.class)); // route to login screen
        }
    }

    private void generateUsers(){
        UserGenerator gen = new UserGenerator(1);
//        Random rand = new Random();
        String email;
        String fName;
        String lName;
        for (int i=0; i<gen.getNUsers(); i++) {
            email = i+"abc@g.com";
            fName = ""+i;
            lName = ""+i;

            gen.uploadUser(email, fName, lName);
        }
    }

    private void generatePosts() {
        PostGenerator gen = new PostGenerator(1, new InitialisationCallback() {
            @Override
            public void onInitialised() {
                // This method will be called when PostGenerator initialization is complete

            }
        });
    }

}