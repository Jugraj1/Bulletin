package com.example.app_2100.data_generators;

import android.util.Log;
import com.example.app_2100.AuthCallback;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.google.firebase.firestore.CollectionReference;
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

    public UserGenerator(int nUsers){
        this.nUsers = nUsers;
    }

    /***
     * We use this code in main activity so that it runs properly
     * @param args
     */
    public static void main(String[] args) {
//        FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
//        UserGenerator gen = new UserGenerator(2);
//        Random rand = new Random();
//        for (int i=0; i<gen.getNUsers(); i++) {
//            // generate post
//            Map<String, Object> user = new HashMap<>();
//            String fName =
//            String lastName =
//
//            user.put("email", getEmail(fName+lName));
//            user.put("firstName", "FIRSTNAME"+i);
//            user.put("lastName", "LASTNAME"+i);
////            uploadUser(email, fName, lName);
//        }
//        System.out.println("USER GEN");

    }

    public String getFirstName(){
        int randomIndex = rand.nextInt(FIRST_NAMES.size());
        return FIRST_NAMES.get(randomIndex);
    }

    public String getLastName(){
        int randomIndex = rand.nextInt(LAST_NAMES.size());
        return LAST_NAMES.get(randomIndex);
    }

    public String getEmail(String name){

        int randomIndex = rand.nextInt(EMAIL_PROVIDERS.size());
        return String.format("%s@%s", name, EMAIL_PROVIDERS.get(randomIndex)).toLowerCase();
    }




    public void uploadUser(String email, String fName, String lName){
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

        CollectionReference usersCollection = FirebaseFirestoreConnection.getDb().collection("users");
        usersCollection.add(user)
                .addOnSuccessListener(documentReference -> {
//                    Log.d("UserGenerator", "Success");
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
