package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app_2100.update.Observer;
import com.example.app_2100.update.UpdatePostView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * Adith Iyer
 */
public class PostViewActivity extends AppCompatActivity implements Observer {

    private Post post;
    private ArrayList<Comment> commentsList;
    private FirebaseFirestore firestore;
    private static final String TAG = "PostView";

/**
 * @param savedInstanceState
 * Adith Iyer
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        // Get the intent that started this activity and retrieve the Parcelable "post" object
        Intent intent = getIntent();
        post = intent.getParcelableExtra("post");

        // Get reference to the like button
        Button likeButton = findViewById(R.id.activity_home_feed_post_thumbnail_bt_like);

        // Get an instance of Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("posts").document(post.getID());

        // Refresh post details
        refreshPost();

        // Retrieve post details from Firestore and update UI
        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> likes = (ArrayList<String>) document.get("likes");
                    String currentUserId = CurrentUser.getCurrent().getUserID();
                    updateLikeButtonUI(likeButton, likes.contains(currentUserId));
                    // Update post likes in Firestore
                    postRef.update("likes", likes);
                }
            }
        });

        // Display post details if available
        if (post != null) {
            displayPostDetails();
        }

        // Initialize Firestore and comments list
        firestore = FirebaseFirestore.getInstance();
        commentsList = new ArrayList<>();

        // Query comments related to the current post and listen for changes
        CollectionReference commentsRef = firestore.collection("comments");
        commentsRef.whereEqualTo("parentID", post.getID())
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener((values, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Error getting comments", error);
                        return;
                    }

                    // Clear existing comments list
                    commentsList.clear();
                    // Iterate through comments and add them to the list
                    for (QueryDocumentSnapshot document : values) {
                        String commentText = document.getString("text");
                        String parentId = document.getString("parentID");
                        Timestamp timestamp = (Timestamp) document.getData().get("timeStamp");
                        commentsList.add(new Comment(parentId, commentText, timestamp));
                    }
                    // Create comments view
                    createComments();
                });

        // Initialize add comment button and handle click events
        Button addCommentButton = findViewById(R.id.activity_postView_btn_addCom);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText commentEditText = findViewById(R.id.activity_postView_et_comment);
                String commentText = commentEditText.getText().toString().trim();

                // Add comment to Firestore if it's not empty
                if (!commentText.isEmpty()) {
                    addCommentToFirestore(commentText);
                    commentEditText.setText("");
                }
            }
        });

        // Handle like button click events
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePostLikesInFirestore(likeButton, post.getID());
            }
        });

        // Initialize view article button and handle click events
        Button viewArticleButton = findViewById(R.id.button3);
        viewArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open WebViewActivity and pass the source URL as an extra
                Intent webViewIntent = new Intent(PostViewActivity.this, ArticleWebViewer.class);
                Log.d(TAG, "url: " + post.getSourceURL());
                webViewIntent.putExtra("url", post.getSourceURL());
                startActivity(webViewIntent);
            }
        });
    }



    /**
     * Creates comment views for each comment in the comments list and adds them to the comment layout
     * Adith Iyer
     */
    private void createComments() {
        // Get the comment layout
        LinearLayout commentLayout = findViewById(R.id.Comments);
        // Iterate through the list of comments
        for (Comment comm : commentsList) {
            // Inflate the comment view layout
            View commentViews = getLayoutInflater().inflate(R.layout.activity_post_view_comment, null);
            // Get references to the text and date TextViews in the comment view layout
            TextView textTv = commentViews.findViewById(R.id.activity_post_view_comment_tv_text);
            TextView dateTv = commentViews.findViewById(R.id.activity_post_view_comment_tv_date);
            // Set the text and date for the comment
            textTv.setText(comm.getText());
            dateTv.setText(comm.getFormattedDateTime());
            // Add the comment view to the comment layout
            commentLayout.addView(commentViews);
        }
    }


    /**
     * Refreshing Posts
     * Adith Iyer
     */
    private void refreshPost() {
        // Instantiate UpdatePostView and set this class as an observer
        UpdatePostView updater = new UpdatePostView(post);
        updater.attach(this);
    }



    /**
     * Displays the details of a post in the activity layout
     * Adith Iyer
     * Jugraj Singh
     */
    private void displayPostDetails() {
        // Find TextViews in the layout by their respective IDs
        TextView titleTextView = findViewById(R.id.activity_postView_tv_Title_1);
        TextView contentTextView = findViewById(R.id.activity_postView_tv_description);
        TextView authorTextView = findViewById(R.id.activity_postView_tv_author);
        TextView dateTextView = findViewById(R.id.activity_postView_tv_timestamp);

        // Set click listener for the authorTextView to open the author's profile
        authorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to open the profile viewer activity and pass the author's ID
                Intent openProfile = new Intent(PostViewActivity.this, ProfileViewer.class);
                openProfile.putExtra("authorID", post.getAuthorID());
                startActivity(openProfile);
            }
        });

        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getBody());
        authorTextView.setText(post.getAuthorName());
        dateTextView.setText(post.getFormattedDateTime());
    }


    /**
     * Adds a comment to Firestore under the "comments" collection
     * @param commentText The text content of the comment to be added
     * Adith Iyer
     */
    private void addCommentToFirestore(String commentText) {
        // Get a reference to the "comments" collection in Firestore
        CollectionReference commentsRef = firestore.collection("comments");

        // Prepare the data to be added to Firestore
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("parentID", post.getID()); // ID of the post to which the comment belongs
        commentData.put("text", commentText); // Text content of the comment
        commentData.put("timeStamp", new Timestamp(new Date())); // Timestamp indicating when the comment was added

        // Add the comment data to Firestore
        commentsRef.add(commentData)
                .addOnSuccessListener(documentReference -> {
                    // If the addition is successful, log a success message
                    Log.d(TAG, "Comment added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // If an error occurs during addition, log a warning with the error details
                    Log.w(TAG, "Error adding comment", e);
                });
    }



    /**
     * Updates the appearance of a like button based on whether it is liked or not
     * @param likeButton The button to update
     * @param isLiked    A boolean indicating whether the button is currently liked or not
     * Adith Iyer
     */
    private void updateLikeButtonUI(Button likeButton, boolean isLiked) {
        // If the button is liked, set its background resource to the liked state drawable
        if (isLiked) {
            likeButton.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
        }
        // If the button is not liked, remove its background to reset it to default
        else {
            likeButton.setBackground(null);
        }
    }



    /**
     * Updates the likes of a post in Firestore based on user interaction
     * @param likeButton The button representing the like action
     * @param postId The ID of the post to update likes for
     * Adith Iyer
     */
    private void updatePostLikesInFirestore(Button likeButton, String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("posts").document(postId);

        // Fetches the current state of the post from Firestore
        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieves the current list of likes for the post.
                    ArrayList<String> likes = (ArrayList<String>) document.get("likes");
                    if (likes == null) {
                        likes = new ArrayList<>();
                    }

                    // Retrieves the ID of the current user
                    String currentUserId = CurrentUser.getCurrent().getUserID();

                    // Checks if the current user has already liked the post
                    if (!likes.contains(currentUserId)) {
                        // Adds the user's ID to the list of likes if not already present
                        likes.add(currentUserId);
                        // Updates the UI to reflect the like action.
                        updateLikeButtonUI(likeButton, true);
                    } else {
                        // Removes the user's ID from the list of likes if already present
                        likes.remove(currentUserId);
                        // Updates the UI to reflect the unlike action
                        updateLikeButtonUI(likeButton, false);
                    }

                    // Updates the Firestore document with the modified list of likes
                    postRef.update("likes", likes);
                }
            }
        });
    }



    /**
     * Refresh the post data, when the Subject detects a change in database (since we are observing for those changes)
     * @param post The new post queried from database
     * @param <T>
     * Noah Vendrig
     */
    @Override
    public <T> void update(T post) {
        Log.d(TAG, "refreshing postview: "+post.toString());
        this.post = (Post) post;
        displayPostDetails();
    }
}