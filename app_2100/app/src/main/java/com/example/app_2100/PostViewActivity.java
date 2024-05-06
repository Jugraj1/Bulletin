package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.Date;

public class PostViewActivity extends AppCompatActivity {
    private Post post;
    private static String TAG = "PostView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        // Retrieve post information from intent (Need to actually get the intent)
        Intent intent = getIntent();
        Post post = (Post) intent.getParcelableExtra("post"); // post is serialised so it can be thrown in intent

        Log.d(TAG, post.toString());
        // Display information in layout
        TextView titleTextView = findViewById(R.id.activity_postView_tv_title);
        TextView contentTextView = findViewById(R.id.activity_postView_tv_description);
        TextView authorTextView = findViewById(R.id.activity_postView_tv_author);
        TextView dateTextView = findViewById(R.id.activity_postView_tv_timestamp);
//        Button URLButton = findViewById(R.id.URL);
        // add a button that leads to the actual post

        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getBody());
        authorTextView.setText(post.getAuthorName());
//        URLButton.setText(post.getSourceURL());
        dateTextView.setText(post.getFormattedDateTime());
//        idTextView.setText(post.getID());
//        publisherTextView.setText(post.getPublisher());

        // OnClickListener for the "View Profile"
        ImageView viewProfileButton = findViewById(R.id.activity_postView_iv_user);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ProfileViewer activity and send authorID as an extra
                Intent profileIntent = new Intent(PostViewActivity.this, ProfileViewer.class);
                profileIntent.putExtra("authorID", post.getAuthorID());
                startActivity(profileIntent);
            }
        });
    }
}
