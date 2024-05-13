package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostViewActivity extends AppCompatActivity {

    private Post post;
    private ArrayList<String> commentsList;
    private FirebaseFirestore firestore;
    private Boolean yes;

    private static final String TAG = "PostView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Intent intent = getIntent();
        post = intent.getParcelableExtra("post");
        yes = intent.getBooleanExtra("yes", true);
        post.setIsLikedByCurrUser(yes);

        if (post != null) {
            displayPostDetails();
        }

        firestore = FirebaseFirestore.getInstance();
        commentsList = new ArrayList<>();
        ListView listViewComments = findViewById(R.id.Comments);
        ArrayAdapter<String> commentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commentsList);
        listViewComments.setAdapter(commentsAdapter);

        CollectionReference commentsRef = firestore.collection("comments");
        commentsRef.whereEqualTo("parentID", post.getID())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Error getting comments", error);
                        return;
                    }

                    commentsList.clear();
                    for (QueryDocumentSnapshot document : value) {
                        String commentText = document.getString("text");
                        commentsList.add(commentText);
                    }
                    commentsAdapter.notifyDataSetChanged();
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

        Button likeButton = findViewById(R.id.activity_home_feed_post_thumbnail_bt_like);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setIsLikedByCurrUser(!post.getLikedByCurrUser());
                updatePostLikesInFirestore(post.getID(), post.getLikedByCurrUser());
                updateLikeButtonUI(likeButton, post.getLikedByCurrUser());
            }
        });

        // Inside your PostViewActivity class

        Button viewArticleButton = findViewById(R.id.button3);
        viewArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open WebViewActivity and pass the source URL as an extra
                Intent webViewIntent = new Intent(PostViewActivity.this, ArticleWebViewer.class);
                webViewIntent.putExtra("url", post.getSourceURL());
                startActivity(webViewIntent);
            }
        });


    }

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

    private void addCommentToFirestore(String commentText) {
        CollectionReference commentsRef = firestore.collection("comments");

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("parentID", post.getID());
        commentData.put("text", commentText);

        commentsRef.add(commentData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Comment added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding comment", e));
    }

    private void updateLikeButtonUI(Button likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
        } else {
            likeButton.setBackground(null);
        }
    }

    private void updatePostLikesInFirestore(String postId, boolean likedByCurrentUser) {
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
                    if (likedByCurrentUser && !likes.contains(currentUserId)) {
                        likes.add(currentUserId);
                    } else if (!likedByCurrentUser && likes.contains(currentUserId)) {
                        likes.remove(currentUserId);
                    }

                    postRef.update("likes", likes);
                }
            }
        });
    }
}




