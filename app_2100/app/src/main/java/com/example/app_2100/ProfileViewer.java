package com.example.app_2100;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileViewer extends AppCompatActivity {
    private FirebaseFirestore db;
    private String loggedInUserID;
    private int currentTab = 0;

    private PostLoadCallback postLoadCallback = new PostLoadCallback() {
        @Override
        public void onPostLoaded(Post post) {
        }
    };

    public void updatePosts(int tab){
        //Clear the layout before populating
        ScrollView scrollView1 = findViewById(R.id.scrollView1);
        LinearLayout scrollViewChildLayout = (LinearLayout) scrollView1.getChildAt(0);
        scrollViewChildLayout.removeAllViews();

        currentTab = tab;
        Log.d("ProfileViewer mytablayoutlistner", "current tab is " + currentTab);
        String authorID = getIntent().getStringExtra("authorID");
        // Query Firebase for posts by that user
        Query postsQuery = db.collection("posts").whereEqualTo("author", authorID);
        postsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> posts = new ArrayList<Post>();
                Map<String, Object> currData;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    currData = document.getData();
                    posts.add(new Post(
                            document.getId(),
                            currData.get("title"),
                            currData.get("body"),
                            currData.get("author"),
                            currData.get("publisher"),
                            currData.get("sourceURL"),
                            currData.get("timeStamp"),
                            postLoadCallback
                    ));
                }
                // Addition to UI through helper
                updateUIWithPosts(posts);
            } else {
                Toast.makeText(ProfileViewer.this, "Failed or no such Posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        db = FirebaseFirestore.getInstance();
        loggedInUserID = CurrentUser.getCurrent().getUserID();

        User user = new User(getIntent().getStringExtra("authorID"), new FirestoreCallback() {
            @Override
            public void onUserLoaded(String fName, String lName, String empty) {
                Log.d("ProfileViewer", "User loaded: " + fName + " " + lName);

                TextView nameTextView = findViewById(R.id.Name);
                nameTextView.setText(fName);

                TextView lastNameTextView = findViewById(R.id.LName);
                lastNameTextView.setText(lName);

            }
        });

        String firstName = user.getFirstName();
        String lastName = user.getLastName();

//        Update textviews with names

//
        // Get Author from extra
        String authorID = getIntent().getStringExtra("authorID");

        // Set onClickListener for Home Button
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileViewer.this, HomeFeed.class);
            startActivity(intent);
        });

        // Set onClickListener for Follow Button
        Button followButton = findViewById(R.id.Follow);
//        followButton.setOnClickListener(view -> followAuthor(authorID));

//        TABS IMPLEMENTATION
        TabLayout myTabLayout = findViewById(R.id.tabLayout);

        myTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updatePosts(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselected event if needed
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselected event if needed
            }});

        updatePosts(0);

    }

    private List<Post> getPosts(){
        List<Post> returnPostsList = new ArrayList<Post>();
        String authorID = getIntent().getStringExtra("authorID");

        // Query Firebase for posts by that user
        Query postsQuery = db.collection("posts").whereEqualTo("author", authorID);
        postsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> posts = new ArrayList<Post>();
                Map<String, Object> currData;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    currData = document.getData();
                    posts.add(new Post(
                            document.getId(),
                            currData.get("title"),
                            currData.get("body"),
                            currData.get("author"),
                            currData.get("publisher"),
                            currData.get("sourceURL"),
                            currData.get("timeStamp"),
                            postLoadCallback
                    ));
                }
                // Addition to UI through helper
            } else {
                Toast.makeText(ProfileViewer.this, "Failed or no such Posts", Toast.LENGTH_SHORT).show();
            }
        });
        return returnPostsList;
    }

    private void updateUIWithPosts(List<Post> posts) {
        LinearLayout layoutToPopulate = null;
        // Determine which ScrollView to use
        // Untested
        ScrollView scrollView1 = findViewById(R.id.scrollView1);
        if (currentTab == 0) {
            Log.d("ProfileViewer", "tabhost.get current tab is " + currentTab);
            for(Post post: posts){
                Log.d("ProfileViewer", "post title: " + post.getTitle());
                addPostToLayout(post, scrollView1);
            }
        } else if (currentTab == 1) {
            Log.d("ProfileViewer", "tabhost.get current tab is " + currentTab);
            displayFollowingUsersOfAuthor(getIntent().getStringExtra("authorID"), scrollView1);
        }
    }

    private void addPostToLayout(Post post, ScrollView layout) {
        // Create a LinearLayout to hold the post content
        LinearLayout postLayout = new LinearLayout(ProfileViewer.this);
        postLayout.setOrientation(LinearLayout.VERTICAL);

        // Create TextView for post title
        TextView titleTextView = new TextView(ProfileViewer.this);
        titleTextView.setText(post.getTitle());
        titleTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        // Set title text properties
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Increase text size
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD); // Make text bold

        // Create TextView for post date
        TextView dateTextView = new TextView(ProfileViewer.this);
        dateTextView.setText(post.getFormattedDateTime());
        dateTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Add title and date TextViews to the postLayout with space in between
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 0); // Add top margin for space between title and date
        postLayout.addView(titleTextView);
        postLayout.addView(dateTextView, layoutParams);

        // Set onClickListener for the postLayout
        postLayout.setOnClickListener(view -> onItemClick(post));

        // Add the postLayout to the ScrollView's child layout with space between posts
        LinearLayout scrollViewChildLayout = (LinearLayout) layout.getChildAt(0);
        LinearLayout.LayoutParams postLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        postLayoutParams.setMargins(0, 0, 0, 32); // Add bottom margin for space between posts
        scrollViewChildLayout.addView(postLayout, postLayoutParams);
    }

    private void displayFollowingUsersOfAuthor(String authorID, ScrollView scrollView) {
        // Retrieve the list of user IDs the profile is following
        db.collection("users").document(authorID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> following = (List<String>) documentSnapshot.get("following");
                    if (following != null) {
                        // Create a LinearLayout to contain all user layouts
                        LinearLayout linearLayout = new LinearLayout(ProfileViewer.this);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);

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
                                        // Set text properties
                                        userTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Increase text size
                                        userTextView.setTypeface(userTextView.getTypeface(), Typeface.BOLD); // Make text bold

                                        // Add space between user details
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(0, 16, 0, 0); // Add top margin for space
                                        userLayout.addView(userTextView, layoutParams);

                                        // Important for re-opening profileViewer with the new user as the subject
                                        // Onclick listener for the user in question
                                        userLayout.setOnClickListener(v -> openProfileViewer(userID));

                                        // Add the userLayout to the LinearLayout
                                        linearLayout.addView(userLayout);

                                        // Add space between user layouts
                                        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                16); // Space height
                                        linearLayout.addView(new Space(ProfileViewer.this), spaceParams);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to retrieve user details.", Toast.LENGTH_SHORT).show());
                        }
                        // Add the LinearLayout to the ScrollView
                        scrollView.removeAllViews();
                        scrollView.addView(linearLayout);
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