package com.example.app_2100;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    private static User instance;
    public String Name;
    public String Last;
    public String Id;
    private static CollectionReference usersRef = FirebaseFirestore.getInstance()
                                         .collection("users");

    private User() {
        // Private constructor to prevent instantiation from outside
    }

    public static User getCurrent() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public static void follow(@NonNull User usr) {
        //the "following" field in the db should also have object references
        //This reference should surely exist as the check profile page led us here.
        DocumentReference userToBeFollowed = usersRef.document(usr.Id);
        DocumentReference currUser = usersRef.document(getCurrent().Id);
        currUser.update("following", FieldValue.arrayUnion(usr));
        userToBeFollowed.update("followers", FieldValue.arrayUnion(currUser));
    }

    //On the user profile, this method should be linked to unfollow
    public static void unFollow(@NonNull User usr) {
        DocumentReference userToBeUnFollowed = usersRef.document(usr.Id);
        DocumentReference currUser = usersRef.document(getCurrent().Id);
        currUser.update("following", FieldValue.arrayRemove(usr));
        userToBeUnFollowed.update("followers", FieldValue.arrayRemove(currUser));
    }
}
