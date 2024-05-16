package com.example.app_2100;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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
import com.example.app_2100.firebase.FirebaseAuthConnection;
import com.example.app_2100.listeners.DataLoadedListener;
import com.example.app_2100.notification.NewLikesNotificationData;
import com.example.app_2100.notification.Notification;
import com.example.app_2100.notification.NotificationFactory;
import com.example.app_2100.notification.NotificationType;
import com.example.app_2100.notification.UpdateWorker;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser currentUser = FirebaseAuthConnection.getCurrentUser();

        // Code used to generate the simulated data:
//        generateUsers(200);
//        generatePosts(2500);

        createNotificationChannel();
//        startUpdateWorker();

        // Check if user is logged in or not, forward to relevant screens
        if (currentUser != null) { // user is logged in already
            startActivity(new Intent(MainActivity.this, HomeFeed.class));
            Log.d(TAG, "logged in already");
        } else { // no currently logged in user
            Intent signoutIntent = new Intent(MainActivity.this, Login.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signoutIntent); // route to login screen
            finish();
        }
    }

    private void startUpdateWorker(){
//        Constraints constraints = new Constraints.Builder()
//                .setRequiresCharging(false)
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                UpdateWorker.class, 15, TimeUnit.MINUTES)
//                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this).enqueue(workRequest);
//        WorkManager.getInstance(this)
//                .getWorkInfoByIdLiveData(workRequest.getId())
//                .observe(this, workInfo -> {
//                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
//                        // Handle the successful completion of the background task
//                    }
//                });
    }
    private static NotificationManager notificationManager;
    public static NotificationManager getNotificationManager(){
        return notificationManager;
    }

    /***
     * Creates the notification channel used by other classes to send notifications
     */
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
     * Used to get permission for sending notifications
     */
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

    /***
     * Generate n users, used to populate the database with simulated users
     * @param n amount of users to generate
     */
    private void generateUsers(int n){
        UserGenerator gen = new UserGenerator(n);


        for (int i=0; i<gen.getNUsers(); i++) {
            String fName = gen.generateFirstName();
            String lName = gen.generateLastName();
            Log.d(TAG, fName+lName);
            gen.generateUsername(fName, lName); // this will upload and get email inside the gen class.
        }
    }
}
