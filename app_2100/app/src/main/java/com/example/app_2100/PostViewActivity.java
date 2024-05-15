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

public class PostViewActivity extends AppCompatActivity implements Observer {

    private Post post;
    private ArrayList<Comment> commentsList;
    private FirebaseFirestore firestore;
    private static final String TAG = "PostView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Intent intent = getIntent();
        post = intent.getParcelableExtra("post");
        System.out.println("before");

        Button likeButton = findViewById(R.id.activity_home_feed_post_thumbnail_bt_like);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("posts").document(post.getID());

        initiateRefresh();

        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> likes = (ArrayList<String>) document.get("likes");
                    String currentUserId = CurrentUser.getCurrent().getUserID();
                    updateLikeButtonUI(likeButton,likes.contains(currentUserId));
                    postRef.update("likes", likes);
                }
            }
        });

        if (post != null) {
            displayPostDetails();
        }

        firestore = FirebaseFirestore.getInstance();
        commentsList = new ArrayList<>();

        CollectionReference commentsRef = firestore.collection("comments");
        commentsRef.whereEqualTo("parentID", post.getID())
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener((values, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Error getting comments", error);
                        return;
                    }

                    commentsList.clear();
                    final int[] i = {values.size()};
                    for (QueryDocumentSnapshot document : values) {
                        String commentText = document.getString("text");
                        String parentID = document.getString("parentID");
                        Timestamp timeStamp = (Timestamp) document.getData().get("timeStamp");

                        commentsList.add(new Comment(parentID, commentText, timeStamp));
                    }
                    generateComments();
                });

        Button addCommentButton = findViewById(R.id.activity_postView_btn_addCom);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText commentEditText = findViewById(R.id.activity_postView_et_comment);
                String commentText = commentEditText.getText().toString().trim();

                if (!commentText.isEmpty()) {
                    addCommentToFirestore(commentText);
                    commentEditText.setText("");
                }
            }
        });
        // Like button updates likes in database
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePostLikesInFirestore(likeButton, post.getID());
            }
        });

        Button viewArticleButton = findViewById(R.id.button3);
        // Open link in browser when clicked
        viewArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open WebViewActivity and pass the source URL as an extra
                Intent webViewIntent = new Intent(PostViewActivity.this, ArticleWebViewer.class);
                Log.d(TAG, "url: "+post.getSourceURL());
                webViewIntent.putExtra("url", post.getSourceURL());
                startActivity(webViewIntent);
            }
        });


    }

    /**
     * Display the comments in the layout
     */
    private void generateComments() {
        LinearLayout commentsLayout = findViewById(R.id.Comments);
        Log.d(TAG, commentsList.toString());
        for (Comment comment : commentsList){
            View commentView = getLayoutInflater().inflate(R.layout.activity_post_view_comment, null);

            TextView textTv = commentView.findViewById(R.id.activity_post_view_comment_tv_text);
//            TextView authorTv = commentView.findViewById(R.id.activity_post_view_comment_tv_author);
            TextView dateTv = commentView.findViewById(R.id.activity_post_view_comment_tv_date);

            textTv.setText(comment.getText());
//            authorTv.setText(comment.getAuthorName());
            dateTv.setText(comment.getFormattedDateTime());

            commentsLayout.addView(commentView);

        }
    }

    /**
     * Setup the Subject which monitors the post updates in firebase, to refresh if theres change
     */
    private void initiateRefresh() {
        // Create a UpdateProfile instance and attach this class as an observer
        UpdatePostView r = new UpdatePostView(post);
        r.attach(this);
    }

    /***
     * Display all the post information on the screen
     */
    private void displayPostDetails() {
        TextView titleTextView = findViewById(R.id.activity_postView_tv_Title);
        TextView contentTextView = findViewById(R.id.activity_postView_tv_description);
        TextView authorTextView = findViewById(R.id.activity_postView_tv_author);
        authorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openProfile = new Intent(PostViewActivity.this, ProfileViewer.class);
                openProfile.putExtra("authorID", post.getAuthorID());
                startActivity(openProfile);
            }
        });
        TextView dateTextView = findViewById(R.id.activity_postView_tv_timestamp);

        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getBody());
        authorTextView.setText(post.getAuthorName());
        dateTextView.setText(post.getFormattedDateTime());
    }

    /***
     * Upload a new comment to the database (postID which the comment belongs to, text of the comment, and the time it was made)
     * @param commentText
     */
    private void addCommentToFirestore(String commentText) {
        CollectionReference commentsRef = firestore.collection("comments");

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("parentID", post.getID());
        commentData.put("text", commentText);
        commentData.put("timeStamp", new Timestamp(new Date()));

        commentsRef.add(commentData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Comment added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding comment", e));
    }

    /**
     * Toggle the like button appearance when clicked/unclicked
     * @param likeButton The like button
     * @param isLiked Whether it is currently liked or not, so it can be toggled
     */
    private void updateLikeButtonUI(Button likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
        } else {
            likeButton.setBackground(null);
        }
    }

    /**
     * Add/Remove likes to a post in the database
     * @param likeButton
     * @param postId
     */
    private void updatePostLikesInFirestore(Button likeButton, String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("posts").document(postId);

        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> likes = (ArrayList<String>) document.get("likes");
                    if (likes == null) {
                        likes = new ArrayList<>();
                    }

                    String currentUserId = CurrentUser.getCurrent().getUserID();
                    if (!likes.contains(currentUserId)) {
                        likes.add(currentUserId);
                        updateLikeButtonUI(likeButton, true);
                    } else if (likes.contains(currentUserId)) {
                        likes.remove(currentUserId);
                        updateLikeButtonUI(likeButton, false);
                    }

                    postRef.update("likes", likes);
                }
            }
        });
    }

    /**
     * Refresh the post data, when the Subject detects a change in database (since we are observing for those changes)
     * @param post The new post queried from database
     * @param <T>
     */
    @Override
    public <T> void update(T post) {
        Log.d(TAG, "refreshing postview: "+post.toString());
        this.post = (Post) post;
        displayPostDetails();
    }
}




