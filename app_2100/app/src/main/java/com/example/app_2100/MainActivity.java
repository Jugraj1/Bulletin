package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.app_2100.data_generators.PostGenerator;
import com.example.app_2100.data_generators.UserGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuthConnection.getCurrentUser();
        FirebaseAuth.getInstance().signOut();

        generateUsers(5);
//        generatePosts(20);

        if(currentUser != null) { // user is logged in already
            startActivity(new Intent(MainActivity.this, HomeFeed.class));
//            Log.d(TAG, "logged in already");
        } else { // no currently logged in user
            startActivity(new Intent(MainActivity.this, Login.class)); // route to login screen
        }
    }

    private void generateUsers(int n){
        UserGenerator gen = new UserGenerator(n);
        String email;
        String fName;
        String lName;
        for (int i=0; i<gen.getNUsers(); i++) {
            fName = gen.getFirstName();
            lName = gen.getLastName();
            email = gen.getEmail(fName+lName);

            gen.uploadUser(email, fName, lName);
        }
    }

    private void generatePosts(int n) {
        PostGenerator gen = new PostGenerator(n, new InitialisationCallback() {
            @Override
            public void onInitialised() {
                Log.d(TAG, "initialised post gen");
                // This method will be called when PostGenerator initialization is complete

            }
        });
    }

}