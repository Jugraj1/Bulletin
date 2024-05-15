package com.example.app_2100.data_generators;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app_2100.App;
import com.example.app_2100.User;
import com.example.app_2100.callbacks.AuthCallback;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.listeners.DataLoadedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserGenerator {

    private int nUsers;
    private Random rand = new Random();

    private final List<String> FIRST_NAMES = Arrays.asList(
            "Noah", "John", "Emma", "Michael", "Olivia", "William", "Ava", "James", "Sophia", "Oliver", "Isabella",
            "Benjamin", "Mia", "Elijah", "Charlotte", "Lucas", "Amelia", "Mason", "Harper", "Logan", "Evelyn"
    );
    private final List<String> LAST_NAMES = Arrays.asList(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    );
    private final List<String> EMAIL_PROVIDERS = Arrays.asList(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com", "icloud.com",
            "protonmail.com", "mail.com"
    );

    private static final String TAG = "UserGenerator";

    private final List<String> firstNames;
    private final List<String> lastNames;

    private String currUsername;
    private String fName;
    private String lName;
    private String email;

    public UserGenerator(int nUsers) {

        this.nUsers = nUsers;
        this.firstNames = App.readTextFile("first_names.txt");
        this.lastNames = App.readTextFile("last_names.txt");
    }

    public String generateUsername(String firstName, String lastName, DataLoadedListener listener){
        int randomNum = rand.nextInt(999);
        String joinedName = firstName+lastName;
        currUsername = String.format("%s%d", firstName+lastName, randomNum).toLowerCase();
        User tempUser = new User("email", firstName, lastName);
        tempUser.checkUsernameExists(currUsername, new DataLoadedListener() {
            @Override
            public void OnDataLoaded(Object exists) {
                Boolean usernameExists = (Boolean) exists;
                if (usernameExists){
                    generateUsername(firstName, lastName, listener);
                } else {
                    listener.OnDataLoaded(currUsername);
                }
            }
        });
        return "";
    }

    public String generateFirstName(){
        int randomIndex = rand.nextInt(firstNames.size());
        this.fName = firstNames.get(randomIndex);
        return fName;
    }

    public String generateLastName(){
        int randomIndex = rand.nextInt(lastNames.size());
        this.lName = lastNames.get(randomIndex);
        return lName;
    }

    public String generateEmail(String name){

        int randomIndex = rand.nextInt(EMAIL_PROVIDERS.size());
        int randomNum = rand.nextInt(99);
        this.email = String.format("%s%d@%s", name, randomNum, EMAIL_PROVIDERS.get(randomIndex)).toLowerCase();
        return email;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public void uploadUser(String email, String fName, String lName, String username){
        // we cant use the usual create account stuff because we're creating lots at once - firebase auth is only supposed to handle the current user
        // its designed so we need to use admin sdk to do stuff like this
        // NEVERMIND firebase admin sdk, has to be used somewhere else on a "secure" location. we cant plonk it in the android app which makes sense
        // but its a bit of a bother to be plonking things in other places and since one git repo gets marked we'll just dodge it by skipping auth - upload record straight to firestore

        // old way
//        FirebaseAuthConnection.getInstance().createAccount(email, "pass123", fName, lName, createAccountCallback()); // give every "fake" user same pass
        //

        // ask noah why:
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
