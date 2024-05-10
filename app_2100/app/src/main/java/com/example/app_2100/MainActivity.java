package com.example.app_2100;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.app_2100.data_generators.PostGenerator;
import com.example.app_2100.data_generators.UserGenerator;
import com.example.app_2100.notification.Notification;
import com.example.app_2100.notification.NotificationFactory;
import com.example.app_2100.notification.NotificationType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuthConnection.getCurrentUser();
//        FirebaseAuth.getInstance().signOut();

//        generateUsers(25);
//        generatePosts(1);


        createNotificationChannel();


        if (currentUser != null) { // user is logged in already
            startActivity(new Intent(MainActivity.this, HomeFeed.class));
            Log.d(TAG, "logged in already");
        } else { // no currently logged in user
            startActivity(new Intent(MainActivity.this, Login.class)); // route to login screen
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notif = NotificationFactory.createNotification(NotificationType.NEW_POST);

            // we need this for android 13 (api 33) and above
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "we have perm");
                // You can use the API that requires the permission.

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.POST_NOTIFICATIONS)) {
                // do stuff
            } else {
                Log.d(TAG, "no perm");
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // for api 33 +
                    requestPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS);
                }
            }

            Log.d(TAG, "sending notif");
            notificationManager.notify(1, notif.getNotificationBuilder().build());
        }
    }

    private void generateUsers(int n){
        UserGenerator gen = new UserGenerator(n);
        String email;
        String fName;
        String lName;
        for (int i=0; i<gen.getNUsers(); i++) {
            fName = gen.getFirstName();
            lName = gen.getLastName();
            email = gen.getEmail(fName+lName);

            gen.uploadUser(email, fName, lName);
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

    private void generatePosts(int n) {
        PostGenerator gen = new PostGenerator(n, new InitialisationCallback() {
            @Override
            public void onInitialised() {
                Log.d(TAG, "initialised post gen");
                // This method will be called when PostGenerator initialization is complete

            }
        });
    }

}