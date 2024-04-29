package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.Date;

public class PostView extends AppCompatActivity {
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
        TextView titleTextView = findViewById(R.id.Title);
        TextView contentTextView = findViewById(R.id.Content);
        TextView authorTextView = findViewById(R.id.Author);
        TextView dateTextView = findViewById(R.id.Date);
        TextView idTextView = findViewById(R.id.ID);
        TextView publisherTextView = findViewById(R.id.Publisher);
        Button URLButton = findViewById(R.id.URL);

        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getBody());
        authorTextView.setText(post.getAuthorName());
        URLButton.setText(post.getSourceURL());
        dateTextView.setText(post.getFormattedDateTime());
        idTextView.setText(post.getID());
        publisherTextView.setText(post.getPublisher());

        // OnClickListener for the "View Profile"
        Button viewProfileButton = findViewById(R.id.ViewProfile);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ProfileViewer activity and send authorID as an extra
                Intent profileIntent = new Intent(PostView.this, ProfileViewer.class);
                profileIntent.putExtra("authorID", post.getAuthorID());
                startActivity(profileIntent);
            }
        });
    }
}
