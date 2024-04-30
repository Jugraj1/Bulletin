package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuthConnection.getCurrentUser();

//        if(currentUser != null) { // user is logged in already
//            startActivity(new Intent(MainActivity.this, HomeFeed.class));
//            Log.d(TAG, "logged in already");
//        } else { // no currently logged in user
//            startActivity(new Intent(MainActivity.this, Login.class)); // route to login screen
//        }

//        startActivity(new Intent(MainActivity.this, HomeFeed.class));
//        startActivity(new Intent(MainActivity.this, PostViewActivity.class));

        startActivity(new Intent(MainActivity.this, Login.class));




        // create instance of firestore DB
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // READ from the database in collection named "posts" - example code, put somewhere else later
//        db.collection("posts")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("Firestore READ success", document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w("Firestore READ error", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
    }
}