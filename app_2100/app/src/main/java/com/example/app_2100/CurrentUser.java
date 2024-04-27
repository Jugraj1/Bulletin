package com.example.app_2100;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CurrentUser extends User{
    // The current user logged into the app
    private static CurrentUser instance;
    private static String TAG = "CurrentUser";
    FirebaseFirestore db = FirebaseFirestoreConnection.getDb().getInstance();

    private CurrentUser() { // Private constructor to prevent instantiation from outside
        FirebaseUser currUser = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
        if (currUser != null){
            this.setUserID(currUser.getUid());
        }

        CollectionReference usersRef = db.collection("users");
        db.collection("users")
            .whereEqualTo(FieldPath.documentId(), this.getUserID())
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

    public static CurrentUser getCurrent() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }
}

