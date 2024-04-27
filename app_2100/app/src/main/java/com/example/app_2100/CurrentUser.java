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

    private FirestoreCallback userCallback = new FirestoreCallback(){
        @Override
        public void onUserLoaded(String fName, String lName){
            setFirstName(fName);
            setLastName(lName);
        }
    };

    private CurrentUser() { // Private constructor to prevent instantiation from outside
        FirebaseUser currUser = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
        if (currUser != null){
            this.setUserID(currUser.getUid()); // set current user id according to current logged in firebase user from auth
            this.queryUserByID(getUserID(), userCallback);
        }

    }

    public static CurrentUser getCurrent() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }
}

