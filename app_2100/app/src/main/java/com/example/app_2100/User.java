package com.example.app_2100;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String userID;
    private String TAG = "User";
    private FirebaseFirestore db = FirebaseFirestoreConnection.getDb().getInstance();



    public User(String userID, FirestoreCallback callback){
        this.userID = userID;

        queryUserByID(this.userID, callback);
    }

//    public FirestoreCallback getUserCallback() {
//        return userCallback;
//    }

    public void queryUserByID(String userID, FirestoreCallback callback){
//        CollectionReference usersRef = db.collection("users");
        db.collection("users")
            .whereEqualTo(FieldPath.documentId(), userID)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) { // todo change this later to ensure only 1 record for user (or we cna imply it from db rules>?)
//                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Map<String, Object> userData = document.getData();
                            String fName = (String) userData.get("firstName");
                            String lName = (String) userData.get("lastName");
                            Log.d(TAG, User.formatName(fName, lName));
                            callback.onUserLoaded(fName, lName);
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    public User(){

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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static String formatName(String fName, String lName){
        return String.format("%s %s", fName, lName); // e.g. "John Smith"
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\''+
                ", userID='" + userID + '\''+
                '}';
    }


}
