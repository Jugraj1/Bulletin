package com.example.app_2100;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.firebase.FirebaseFirestoreConnection;
import com.example.app_2100.notification.NewPostNotificationData;
import com.example.app_2100.notification.Notification;
import com.example.app_2100.notification.NotificationFactory;
import com.example.app_2100.notification.NotificationType;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Adith Iyer
 * Noah Vendrig
 * Jugraj Singh
 */

public class CreatePost extends AppCompatActivity {
    private EditText titleEditText, publisherEditText, urlEditText, contentEditText;
    private Button createButton;
    private FirebaseFirestore db;


    /**
     * @param savedInstanceState
     * Adith Iyer
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Firestore initialization
        db = FirebaseFirestoreConnection.getDb();

        // Create notification channel for notifications
        createNotificationChannel();

        // Initialize views
        initViews();

        // Set onClick listeners for buttons
        setCreateButtonClickListener();
        setGoBackButtonClickListener();
    }

    /**
     * Initialize views by finding them from layout XML
     * Adith Iyer
     */
    private void initViews() {
         publisherEditText = findViewById(R.id.activity_create_post_et_publisher);
         urlEditText = findViewById(R.id.activity_create_post_et_url);
         createButton = findViewById(R.id.activity_create_post_bt_submit);
        titleEditText = findViewById(R.id.activity_create_post_et_title);
        contentEditText = findViewById(R.id.activity_create_post_et_content);
    }

    /**
     * Set onClick listener for the create post button
     * This listener validates input fields and creates a new post
     * Adith Iyer
     */
    private void setCreateButtonClickListener() {
        View.OnClickListener createClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String URL = urlEditText.getText().toString().trim();

                String heading = titleEditText.getText().toString().trim();

                String text = contentEditText.getText().toString().trim();

                String publisher = publisherEditText.getText().toString().trim();

                // Check fields
                if (publisher.isEmpty() || URL.isEmpty() || text.isEmpty() || heading.isEmpty()) {
                    Toast.makeText(CreatePost.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return;                }
                // Generate Post Data and create post
                createPost(heading, publisher, URL, text);
            }
        };

        createButton.setOnClickListener(createClickListener);
    }


    /**
     * Create a new post with the provided data and add it to the Firestore database
     * @param title     Title of the post
     * @param publisher Publisher of the post
     * @param url       URL associated with the post
     * @param content   Content/body of the post
     * Adith Iyer
     */
    private void createPost(String title, String publisher, String url, String content) {
        Map<String, Object> post = new HashMap<>();
        Timestamp currTime = new Timestamp(new Date());
        String currUserID = CurrentUser.getCurrent().getUserID();

// Populate post data
        post.put("timeStamp", currTime);
        post.put("author", currUserID);
        post.put("publisher", publisher);
        post.put("title", title);
        post.put("body", content);
        post.put("url", url);
        post.put("likes", Collections.emptyList()); // empty likes array, needed for ordering
        post.put("score", 0.0); // score will be calculated by cloud function


        // Add post to "posts" collection in Firestore
        CollectionReference postsCollection = db.collection("posts");
        postsCollection.add(post)
                .addOnSuccessListener(documentReference -> {
                    handlePostCreationSuccess(documentReference);
                })
                .addOnFailureListener(e -> {
                    handlePostCreationFailure();
                });
    }

    /**
     * Handle successful creation of a post.
     * This method is called when a post is successfully added to the Firestore database
     * @param documentReference Reference to the document of the newly created post
     * Noah Vendrig and Adith Iyer
     */
    private void handlePostCreationSuccess(DocumentReference documentReference) {
        // Show toast notification for successful post creation
        Toast.makeText(CreatePost.this, "Post created successfully", Toast.LENGTH_SHORT).show();

        // Prepare intent to view the newly created post
        Intent postViewIntent = new Intent(this, PostViewActivity.class);
        Context context = this;

        // Extract necessary data for the post
        String authorID = documentReference.getId();
        Post uploadedPost = new Post(
                authorID,
                titleEditText.getText().toString(),
                contentEditText.getText().toString(),
                CurrentUser.getCurrent().getUserID(),
                publisherEditText.getText().toString(),
                urlEditText.getText().toString(),
                new Timestamp(new Date()),
                new PostLoadCallback() {
                    @Override
                    public void onPostLoaded(Post loadedPost) {
                        // Add post data to intent for viewing
                        postViewIntent.putExtra("post", loadedPost);

                        // Set flags for intent
                        postViewIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Create pending intent used for the notification to open the post
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context, 0,
                                postViewIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        NewPostNotificationData data = new NewPostNotificationData(loadedPost, NotificationType.NEW_POST, pendingIntent);

                        // Create and display notification for the new post
                        Notification postCreatedNotif = NotificationFactory.createNotification(data);
                        MainActivity.getNotificationManager().notify(2, postCreatedNotif.getNotificationBuilder().build());

                        // Open the newly created post and finish this activity
                        startActivity(postViewIntent);
                        finish();
                    }
                }
        );
    }

    /**
     * Handle failure in post creation.
     * This method is called when there is an error while adding a post to the Firestore database
     * Adith Iyer
     */
    private void handlePostCreationFailure() {
        // Show toast notification for failure in post creation
        Toast.makeText(CreatePost.this, "Failed to create post", Toast.LENGTH_SHORT).show();
    }

    /**
     * Set onClick listener for the "Go Back" button
     * This listener navigates back to the home feed activity when clicked
     * Jugraj Singh
     */
    private void setGoBackButtonClickListener() {
        Button goBackBt = findViewById(R.id.activity_search_bt_go_back);
        goBackBt.setOnClickListener(v -> {
            startActivity(new Intent(CreatePost.this, HomeFeed.class));
        });
    }

    /**
     * Creates a notification channel for displaying notifications, but only on devices running
     * Android API level 26 (Android 8.0, Oreo) or higher
     * Noah Vendrig
     */
    private void createNotificationChannel() {
        // Define the channel ID
        String CHANNEL_ID = "channel_1";

        // Check if the device's API level is 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get the name and description of the notification channel from string resources
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            // Set the importance level of the notification channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Create the notification channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Enable vibration for notifications on this channel
            channel.enableVibration(true);

            // Register the notification channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // Check for the POST_NOTIFICATIONS permission
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, perform necessary actions

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Permission was previously denied by the user, handle the rationale

            } else {
                // Permission has not been granted, request it from the user
                String TAG = "CreatePost";
                Log.d(TAG, "no perm");
                // Check if the device's API level is 33 or higher (Android 13 or later)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Launch the permission request
                    requestPermissionLaunch.launch(
                            Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        }
    }

    /**
     * Activity result launcher for requesting the POST_NOTIFICATIONS permission.
     * Handles the result of the permission request.
     * Noah Vendrig and Adith Iyer
     */
    private final ActivityResultLauncher<String> requestPermissionLaunch =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Handle the result of the permission request
                // If permission is granted, continue with the action or workflow in the app
                // If permission is denied, explain to the user that the feature is unavailable
                // and respect the user's decision without prompting them to change it.
            });

}
