package com.example.app_2100;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app_2100.notification.NewPostNotificationData;
import com.example.app_2100.notification.Notification;
import com.example.app_2100.notification.NotificationFactory;
import com.example.app_2100.notification.NotificationType;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePost extends AppCompatActivity {

    private EditText titleEditText, publisherEditText, urlEditText, contentEditText;
    private Button createButton;
    private FirebaseFirestore db;

    private Post currPost;
    private static String TAG = "CreatePost";
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Firestore init
        db = FirebaseFirestoreConnection.getDb();

        createNotificationChannel();

        // Views init
        titleEditText = findViewById(R.id.activity_create_post_et_title);
        publisherEditText = findViewById(R.id.activity_create_post_et_publisher);
        urlEditText = findViewById(R.id.activity_create_post_et_url);
        contentEditText = findViewById(R.id.activity_create_post_et_content);
        createButton = findViewById(R.id.activity_create_post_bt_submit);

        // OnClick Listener
        createButton.setOnClickListener(v -> {

            String title = titleEditText.getText().toString().trim();
            String publisher = publisherEditText.getText().toString().trim();
            String url = urlEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();


            // Check Fields
            if (title.isEmpty() || publisher.isEmpty() || url.isEmpty() || content.isEmpty()) {
                Toast.makeText(CreatePost.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            currPost = new Post(title, content, CurrentUser.getCurrent().getUserID(), publisher, url, new Timestamp(new Date()));
            // Generate Post Data
            Map<String, Object> post = new HashMap<>();
            post.put("title", currPost.getTitle());
            post.put("publisher", currPost.getPublisher());
            post.put("url", currPost.getSourceURL());
            post.put("body", currPost.getBody());
            post.put("author", currPost.getAuthorID());
            post.put("timeStamp", currPost.getTimeStamp());
            post.put("likes", Collections.emptyList()); // empty likes arr, we need it to exist so we can OrderBy
            post.put("score", 0.0); // empty likes arr, we need it to exist so we can OrderBy


            // Add to "posts" collection in firestore
            CollectionReference postsCollection = db.collection("posts");
            postsCollection.add(post)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Created post!");
                        Toast.makeText(CreatePost.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                        NewPostNotificationData data = new NewPostNotificationData(currPost, NotificationType.NEW_POST);

                        Notification postCreatedNotif = NotificationFactory.createNotification(data);
//                        MainActivity.getNotificationManager().notify(2, postCreatedNotif.getNotificationBuilder().build());
//                        notificationManager.notify(2, postCreatedNotif.getNotificationBuilder().build());
                        MainActivity.getNotificationManager().notify(2, postCreatedNotif.getNotificationBuilder().build());
                        finish(); // Finish the activity after creating the post
                    })
                    .addOnFailureListener(e -> {
                        // boohoo
                        Toast.makeText(CreatePost.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    });
        });

        Button goBackBt = findViewById(R.id.activity_search_bt_go_back);
        goBackBt.setOnClickListener(v -> {
            startActivity(new Intent(CreatePost.this, HomeFeed.class));
        });
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
            channel.canBubble();
            channel.enableVibration(true);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

//            Notification notif = NotificationFactory.createNotification(NotificationType.NEW_POST);

            // we need this for android 13 (api 33) and above
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "we have perm");
                // You can use the API that requires the permission.

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.POST_NOTIFICATIONS)) {
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
//            notificationManager.notify(1, notif.getNotificationBuilder().build());
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
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
}
