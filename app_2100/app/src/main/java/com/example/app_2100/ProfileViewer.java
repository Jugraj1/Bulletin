package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewer extends AppCompatActivity {

    private String authorID;
    private ListView postListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> postTitles;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        // Get authorID from intent extra
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            authorID = extras.getString("authorID");
        }

        postListView = findViewById(R.id.PostList);
        postTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, postTitles);
        postListView.setAdapter(adapter);

        // Query Firebase Firestore for posts by authorID
        Query postsQuery = db.collection("posts").whereEqualTo("author", authorID);
        postsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    postTitles.add(title);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ProfileViewer.this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

