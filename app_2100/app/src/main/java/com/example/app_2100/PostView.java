package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.Date;

public class PostView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        // Retrieve post information from intent (Need to actually get the intent)

        // Noah and Jugraj, y'all need to help me with this

        Intent intent = getIntent();
        if (intent != null) {
            String postId = intent.getStringExtra("postId");
            String postTitle = intent.getStringExtra("postTitle");
            String postBody = intent.getStringExtra("postBody");
            String postAuthorID = intent.getStringExtra("postAuthorID");
            String postAuthor = intent.getStringExtra("postAuthor");
            String postPublisher = intent.getStringExtra("postPublisher");
            String postURL = intent.getStringExtra("postURL");
            String postDate = intent.getStringExtra("postDate");


            // Display information in layout
            TextView titleTextView = findViewById(R.id.Title);
            TextView contentTextView = findViewById(R.id.Content);
            TextView authorTextView = findViewById(R.id.Author);
            TextView dateTextView = findViewById(R.id.Date);
            TextView idTextView = findViewById(R.id.ID);
            TextView publisherTextView = findViewById(R.id.Publisher);
            Button URLButton = findViewById(R.id.URL);

            titleTextView.setText(postTitle);
            contentTextView.setText(postBody);
            authorTextView.setText(postAuthor);
            URLButton.setText(postURL);
            dateTextView.setText(postDate);
            idTextView.setText(postId);
            publisherTextView.setText(postPublisher);

            // OnClickListener for the "View Profile"
            Button viewProfileButton = findViewById(R.id.ViewProfile);
            viewProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start ProfileViewer activity and send authorID as an extra
                    Intent profileIntent = new Intent(PostView.this, ProfileViewer.class);
                    profileIntent.putExtra("authorID", postAuthorID);
                    startActivity(profileIntent);
                }
            });
        }
    }
}
