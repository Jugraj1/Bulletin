package com.example.app_2100;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePost extends AppCompatActivity {

    private EditText titleEditText, publisherEditText, urlEditText, contentEditText;
    private Button createButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Firestore init
        db = FirebaseFirestoreConnection.getDb();

        // Views init
        titleEditText = findViewById(R.id.editTextText);
        publisherEditText = findViewById(R.id.editTextText2);
        urlEditText = findViewById(R.id.editTextText3);
        contentEditText = findViewById(R.id.editTextTextMultiLine2);
        createButton = findViewById(R.id.button);

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
            post.put("title", title);
            post.put("publisher", publisher);
            post.put("url", url);
            post.put("content", content);
            post.put("Author", User.getCurrent().Id);


            // Add to "posts" collection in firestore IDK if this works lmaoo
            CollectionReference postsCollection = db.collection("posts");
            postsCollection.add(post)
                    .addOnSuccessListener(documentReference -> {
                        // check success
                        Toast.makeText(CreatePost.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after creating the post
                    })
                    .addOnFailureListener(e -> {
                        // boohoo
                        Toast.makeText(CreatePost.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
