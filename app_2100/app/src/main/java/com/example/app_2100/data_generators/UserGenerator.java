package com.example.app_2100.data_generators;

import android.util.Log;

import com.example.app_2100.App;
import com.example.app_2100.User;
import com.example.app_2100.callbacks.AuthCallback;
import com.example.app_2100.firebase.FirebaseFirestoreConnection;
import com.example.app_2100.listeners.DataLoadedListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserGenerator {

    private int nUsers;
    private Random rand = new Random();

    private final List<String> EMAIL_PROVIDERS = Arrays.asList(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com", "icloud.com",
            "protonmail.com", "mail.com"
    );

    private static final String TAG = "UserGenerator";

    private final List<String> firstNames;
    private final List<String> lastNames;

    public UserGenerator(int nUsers) {

        this.nUsers = nUsers;
        this.firstNames = App.readTextFile("first_names.txt");
        this.lastNames = App.readTextFile("last_names.txt");
    }

    public void generateUsername(String firstName, String lastName){
        int randomNum = rand.nextInt(999);
        String joinedName = firstName+lastName;
        String currUsername = String.format("%s%d", joinedName, randomNum).toLowerCase();
        User tempUser = new User("email", firstName, lastName);
        tempUser.checkUsernameExists(currUsername, new DataLoadedListener() {
            @Override
            public void OnDataLoaded(Object exists) {
                Boolean usernameExists = (Boolean) exists;
                if (usernameExists){
                    generateUsername(firstName, lastName);
                } else {
                    uploadUser(generateEmail(firstName+lastName),firstName, lastName,currUsername);
//                    listener.OnDataLoaded(currUsername);
                }
            }
        });
    }

    public String generateFirstName(){
        int randomIndex = rand.nextInt(firstNames.size());
//        this.fName = firstNames.get(randomIndex);
        return firstNames.get(randomIndex);
    }

    public String generateLastName(){
        int randomIndex = rand.nextInt(lastNames.size());
//        this.lName = lastNames.get(randomIndex);
        return lastNames.get(randomIndex);
    }

    public String generateEmail(String name){

        int randomIndex = rand.nextInt(EMAIL_PROVIDERS.size());
        int randomNum = rand.nextInt(99);
//        this.email = String.format("%s%d@%s", name, randomNum, EMAIL_PROVIDERS.get(randomIndex)).toLowerCase();
        return String.format("%s%d@%s", name, randomNum, EMAIL_PROVIDERS.get(randomIndex)).toLowerCase();
    }


    public void uploadUser(String email, String fName, String lName, String username){
        // we cant use the usual create account stuff because we're creating lots at once - firebase auth is only supposed to handle the current user
        // its designed so we need to use admin sdk to do stuff like this
        // NEVERMIND firebase admin sdk, has to be used somewhere else on a "secure" location. we cant plonk it in the android app which makes sense
        // but its a bit of a bother to be plonking things in other places and since one git repo gets marked we'll just dodge it by skipping auth - upload record straight to firestore

        // dont create acc in auth - skip straight to firestore
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("firstName", fName);
        user.put("lastName", lName);
        user.put("username", username);

        CollectionReference usersCollection = FirebaseFirestoreConnection.getDb().collection("users");
        usersCollection.add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d("UserGenerator", "Successfully created user with id: "+documentReference.getId() + " / username: "+username);
                })
                .addOnFailureListener(e -> {
                    // boohoo
                    Log.e("UserGenerator","ERR uploading user: "+ e);
                });
    }

    public int getNUsers() {
        return nUsers;
    }

}
