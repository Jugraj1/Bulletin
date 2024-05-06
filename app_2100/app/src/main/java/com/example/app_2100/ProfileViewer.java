package com.example.app_2100;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.example.app_2100.Post;
import com.example.app_2100.PostViewActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProfileViewer extends AppCompatActivity {


    private FirebaseFirestore db;
    private String loggedInUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

//
//        db = FirebaseFirestore.getInstance();
//        loggedInUserID = CurrentUser.getCurrent().getUserID();
//
//
//        // Get Author from extra
//        String authorID = getIntent().getStringExtra("authorID");
//
//
//        // Query Firebase for posts by that user
//        Query postsQuery = db.collection("posts").whereEqualTo("author", authorID);
//        postsQuery.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<Post> posts = new ArrayList<Post>();
//                Map<String, Object> currData;
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    currData = document.getData();
//                    posts.add(new Post(
//                            document.getId(),
//                            currData.get("title"),
//                            currData.get("body"),
//                            currData.get("author"),
//                            currData.get("publisher"),
//                            currData.get("sourceURL"),
//                            currData.get("timeStamp")
//                    ));
//                }
//                // Addition to UI through helper
//                updateUIWithPosts(posts);
//            } else {
//                Toast.makeText(ProfileViewer.this, "Failed or no such Posts", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        // Set onClickListener for Home Button
//        Button homeButton = findViewById(R.id.homeButton);
//        homeButton.setOnClickListener(view -> {
//            Intent intent = new Intent(ProfileViewer.this, HomeFeed.class);
//            startActivity(intent);
//        });
//
//
//        // Set onClickListener for Follow Button
//        Button followButton = findViewById(R.id.Follow);
//        followButton.setOnClickListener(view -> followAuthor(authorID));
    }


    private void updateUIWithPosts(List<Post> posts) {
        TabHost tabHost = findViewById(R.id.tabHost);
        LinearLayout layoutToPopulate = null;

        // Determine which ScrollView to use
        // Untested
        if (tabHost.getCurrentTab() == 0) {
            ScrollView scrollView1 = findViewById(R.id.scrollView1);
            layoutToPopulate = scrollView1.findViewById(R.id.postsLayout1);
        } else if (tabHost.getCurrentTab() == 1) {
            ScrollView scrollView2 = findViewById(R.id.scrollView2);
            layoutToPopulate = scrollView2.findViewById(R.id.postsLayout2);
        }

        if (layoutToPopulate != null) {
            layoutToPopulate.removeAllViews(); // Clear the layout before populating

            if (tabHost.getCurrentTab() == 0) {
                // Display posts
                for (Post post : posts) {
                    addPostToLayout(post, layoutToPopulate);
                }
            } else if (tabHost.getCurrentTab() == 1) {
                // Display following users
                String authorID = getIntent().getStringExtra("authorID");
                displayFollowingUsersOfAuthor(authorID, layoutToPopulate);
            }
        }
    }

    private void addPostToLayout(Post post, LinearLayout layout) {
        TextView titleTextView = new TextView(ProfileViewer.this);
        titleTextView.setText(post.getTitle());
        titleTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView dateTextView = new TextView(ProfileViewer.this);
        dateTextView.setText(post.getFormattedDateTime());
        dateTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Add both title and dateTextView to a layout
        LinearLayout postLayout = new LinearLayout(ProfileViewer.this);
        postLayout.setOrientation(LinearLayout.VERTICAL);
        postLayout.addView(titleTextView);
        postLayout.addView(dateTextView);

        // Set onClickListener for the postLayout
        postLayout.setOnClickListener(view -> onItemClick(post));

        // Add the postLayout to the specified layout
        layout.addView(postLayout);
    }

    private void displayFollowingUsersOfAuthor(String authorID, LinearLayout layout) {
        // Retrieve the list of user IDs the profile is following
        db.collection("users").document(authorID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> following = (List<String>) documentSnapshot.get("following");
                    if (following != null) {
                        for (String userID : following) {
                            // Retrieve user details
                            db.collection("users").document(userID).get()
                                    .addOnSuccessListener(userDocument -> {
                                        // New layout to display relevant user details
                                        LinearLayout userLayout = new LinearLayout(ProfileViewer.this);
                                        userLayout.setOrientation(LinearLayout.VERTICAL);

                                        String firstName = userDocument.getString("firstName");
                                        String lastName = userDocument.getString("lastName");

                                        // Display user details in a TextView
                                        TextView userTextView = new TextView(ProfileViewer.this);
                                        userTextView.setText(firstName + " " + lastName);
                                        userTextView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));

                                        // Important for re-opening profileViewer with the new user as the subject
                                        // Onclick listener for the user in question
                                        userLayout.setOnClickListener(v -> openProfileViewer(userID));

                                        // Add the TextView to the new layout
                                        userLayout.addView(userTextView);

                                        // Add the userLayout to the ScrollView
                                        layout.addView(userLayout);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to retrieve user details.", Toast.LENGTH_SHORT).show());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to retrieve following list.", Toast.LENGTH_SHORT).show());
    }


    // Intent to view profile of the clicked user
    private void openProfileViewer(String userID) {
        Intent profileViewerIntent = new Intent(ProfileViewer.this, ProfileViewer.class);
        profileViewerIntent.putExtra("authorID", userID);
        startActivity(profileViewerIntent);
    }

    // Intent back to PostView
    private void onItemClick(Post post) {
        Intent postViewIntent = new Intent(ProfileViewer.this, PostViewActivity.class);
        postViewIntent.putExtra("post", post);
        startActivity(postViewIntent);
    }


    // Method to follow the profile's Author
    private void followAuthor(String authorID) {
        // Search for the profile of the logged-in user
        db.collection("users").document(loggedInUserID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> following = (List<String>) documentSnapshot.get("following");
                    if (following == null) {
                        following = new ArrayList<>();
                    }
                    // cant follow yourself
                    if (authorID.equals(loggedInUserID)){
                        Toast.makeText(ProfileViewer.this, "You cant follow yourself.", Toast.LENGTH_SHORT).show();
                    }
                    // Check to see if not already following
                    else if (!following.contains(authorID)) {
                        // If not, add it to the list
                        following.add(authorID);
                        db.collection("users").document(loggedInUserID).update("following", following)
                                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileViewer.this, "You are now following this user.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to follow user.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(ProfileViewer.this, "You are already following this user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to retrieve user profile.", Toast.LENGTH_SHORT).show());
    }
}


