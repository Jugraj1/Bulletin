package com.example.app_2100;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class User {
    private static User instance;
    private String firstName;
    private String lastName;

    private String userID;
    private static String TAG = "User";
    FirebaseFirestore db = FirebaseFirestoreConnection.getDb().getInstance();

    private User() { // Private constructor to prevent instantiation from outside
        FirebaseUser currUser = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
        if (currUser != null){
            this.userID = currUser.getUid();
        }

        CollectionReference usersRef = db.collection("users");
        db.collection("users")
                .whereEqualTo(FieldPath.documentId(), userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static User getCurrent() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }
}

