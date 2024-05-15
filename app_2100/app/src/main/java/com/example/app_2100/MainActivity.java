package com.example.app_2100;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.app_2100.callbacks.InitialisationCallback;
import com.example.app_2100.data_generators.PostGenerator;
import com.example.app_2100.data_generators.UserGenerator;
import com.example.app_2100.listeners.DataLoadedListener;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

//    TODO: List of known bugs as of 15/05/24
//    FIXME: - When searching yields no results, the app crashes
//    FIXME: - When searching through posts, the posts cant be clicked on to open it
//    FIXME: - Cant search for users

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuthConnection.getCurrentUser();
//        TODO: All this commented out code is for testing purposes only. REMOVE it before final release
//        FirebaseAuth.getInstance().signOut();

//        generateUsers(1);
//        generatePosts(1);

        createNotificationChannel();


        if (currentUser != null) { // user is logged in already
            startActivity(new Intent(MainActivity.this, HomeFeed.class));
            Log.d(TAG, "logged in already");
        } else { // no currently logged in user
            startActivity(new Intent(MainActivity.this, Login.class)); // route to login screen
            finish();
        }
    }

    private static NotificationManager notificationManager;
    public static NotificationManager getNotificationManager(){
        return notificationManager;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        String CHANNEL_ID = "channel_1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // standard docs code
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // we need this for android 13 (api 33) and above
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted for 'POST_NOTIFICATIONS'");

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                Log.d(TAG, "No permission for 'POST_NOTIFICATIONS'");
                // Directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // for api 33 +
                    requestPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        }
    }

    /***
     * Generate n users, used to populate the database with simulated users
     * @param n amount of users to generate
     */
    private void generateUsers(int n){
        UserGenerator gen = new UserGenerator(n);


        for (int i=0; i<gen.getNUsers(); i++) {

            gen.generateUsername(gen.generateFirstName(), gen.generateLastName(), new DataLoadedListener() {
                @Override
                public void OnDataLoaded(Object currUsername) {
                    String fName = gen.getfName();
                    String lName = gen.getlName();
                    String email = gen.generateEmail(fName+lName);
                    String username = (String) currUsername;
                    gen.uploadUser(email, fName, lName, username);
                }
            });


        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    /***
     * Generate n posts, used to populate the database with simulated posts
     * @param n Amount of posts to generate
     */
    private void generatePosts(int n) {
        PostGenerator gen = new PostGenerator(n, new InitialisationCallback() {
            @Override
            public void onInitialised() {
                Log.d(TAG, "initialised post generator");
            }
        });
    }

}