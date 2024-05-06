package com.example.app_2100.data_generators;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app_2100.AuthCallback;
import com.example.app_2100.CreateAccount;
import com.example.app_2100.CurrentUser;
import com.example.app_2100.FirebaseAuthConnection;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.HomeFeed;
import com.example.app_2100.Post;
import com.example.app_2100.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Date;
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
        return String.format("%s@%s", name, EMAIL_PROVIDERS.get(randomIndex));
    }




    public void uploadUser(String email, String fName, String lName){
        FirebaseAuthConnection.getInstance().createAccount(email, "pass123", fName, lName, createAccountCallback()); // give every "fake" user same pass
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
