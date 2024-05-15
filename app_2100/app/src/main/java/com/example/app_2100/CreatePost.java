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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app_2100.callbacks.PostLoadCallback;
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

            // Generate Post Data
            Map<String, Object> post = new HashMap<>();
            Timestamp currTime = new Timestamp(new Date());
            String currUserID = CurrentUser.getCurrent().getUserID();

            post.put("title", title);
            post.put("publisher", publisher);
            post.put("url", url);
            post.put("body", content);
            post.put("author", currUserID);
            post.put("timeStamp", currTime);
            post.put("likes", Collections.emptyList()); // empty likes arr, we need it to exist so we can OrderBy
            post.put("score", 0.0); // empty likes arr, we need it to exist so we can OrderBy

            // Add to "posts" collection in firestore
            CollectionReference postsCollection = db.collection("posts");
            postsCollection.add(post)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CreatePost.this, "Post created successfully", Toast.LENGTH_SHORT).show();

                        Intent postViewIntent = new Intent(this, PostViewActivity.class);
                        Context context = this;

                        String authorID = documentReference.getId();
                        Post uploadedPost = new Post(
                                authorID,
                                title,
                                content,
                                currUserID,
                                publisher,
                                url,
                                currTime,
                                new PostLoadCallback() {
                                    @Override
                                    public void onPostLoaded(Post loadedPost) {
                                        postViewIntent.putExtra("post", loadedPost);

                                        postViewIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        // Create pending intent used for then notification to open the post
                                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                                context, 0,
                                                postViewIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );
                                        NewPostNotificationData data = new NewPostNotificationData(loadedPost, NotificationType.NEW_POST, pendingIntent);

                                        Notification postCreatedNotif = NotificationFactory.createNotification(data);
//                        notificationManager.notify(2, postCreatedNotif.getNotificationBuilder().build());
                                        MainActivity.getNotificationManager().notify(2, postCreatedNotif.getNotificationBuilder().build()); // create notification

                                        startActivity(postViewIntent); // go to the post they just created
                                        finish(); // Finish the activity after creating the post
                                    }
                                }
                        );

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

    private final PostLoadCallback postLoadCallback = new PostLoadCallback() {
        @Override
        public void onPostLoaded(Post post) {

        }
    };

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

            // Register the channel with the system, can't change the importance or other notification behaviors after this.
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // we need this for android 13 (api 33) and above
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
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
