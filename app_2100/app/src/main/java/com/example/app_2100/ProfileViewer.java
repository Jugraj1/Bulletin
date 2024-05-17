package com.example.app_2100;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import com.example.app_2100.callbacks.FirestoreCallback;
import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.firebase.FirebaseAuthConnection;
import com.example.app_2100.update.Observer;
import com.example.app_2100.update.UpdateProfile;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Adith Iyer
 */

public class ProfileViewer extends AppCompatActivity implements Observer {
    private FirebaseFirestore db;
    private String loggedInUserID;
    private int currentTab = 0;
    private User user;

    private String userID;
    private final static String TAG = "ProfileViewer";

/**
 * Initializes the activity and sets up UI components and event listeners.
 * @param savedInstanceState
 * Adith Iyer
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer); // Set activity layout
        db = FirebaseFirestore.getInstance(); // Initialize FireStore
        userID = getIntent().getStringExtra("authorID"); // Get authorID from Intent
        loggedInUserID = FirebaseAuthConnection.getAuth().getUid(); // Get logged-in user's ID

        fetchUser(userID); // Fetch user details from FireStore

        // Set onClickListener for Home Button to navigate to HomeFeed activity
        findViewById(R.id.homeButton).setOnClickListener(view -> {
            startActivity(new Intent(ProfileViewer.this, HomeFeed.class));
        });

        // Initialize views
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Button followButton = findViewById(R.id.Follow);
        Button signOutButton = findViewById(R.id.Signout);

        // Adjust UI based on whether the profile belongs to the logged-in user
        if (userID.equals(loggedInUserID)) {
            followButton.setVisibility(View.GONE); // Hide follow button
            signOutButton.setVisibility(View.VISIBLE); // Show sign out button
            // Set onClickListener for SignOut button
            signOutButton.setOnClickListener(view -> signOut());

            // Set tab text for the current user's profile
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText("My Posts");
        } else {
            followButton.setVisibility(View.VISIBLE); // Show follow button
            signOutButton.setVisibility(View.GONE); // Hide sign out button

            // Set onClickListener for Follow button
            followButton.setOnClickListener(view -> followAuthor(userID));
        }

        // Add listener for tab selection events to display posts accordingly
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getPosts(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // Display posts for the initial tab (index 0)
        getPosts(0);
    }


    /**
     * Signs out the current user and navigates to the login screen.
     * Noah Vendrig
     */
    private void signOut() {
        // Sign out the current user
        FirebaseAuthConnection.getAuth().signOut();
        startActivity(new Intent(ProfileViewer.this, Login.class));
        finish();
    }

    /**
     * Fetches user data from FireStore based on the provided user ID
     * Initializes the user object and performs actions once the user data is loaded
     * @param userID The ID of the user to fetch
     * Adith Iyer
     */
    private void fetchUser(String userID) {
        // Create a new User object with the provided user ID
        user = new User(userID, new FirestoreCallback() {
            @Override //
            public void onUserLoaded(String fName, String lName, String empty, String username) {
                // Log a message indicating that the user data has been loaded
                Log.d("ProfileViewer", "User loaded:  " + fName + " " + lName);
                // Update the UI with user data
                updateUserTexts();
                // Initiate the refresh process for the user's profile
                startRefresh();
                // Update the user's profile picture
                updateProfilePic();
            }
        });
    }


    /**
     * Updates the text views with user data.
     * Retrieves the user's first name, last name, and username from the user object
     * and sets them in the corresponding text views.
     * Adith Iyer
     */
    private void updateUserTexts() {
        // Find and update the first name text view
        TextView nameView = findViewById(R.id.Name);
        nameView.setText(user.getFirstName());

        // Find and update the last name text view
        TextView lastNameView = findViewById(R.id.LName);
        lastNameView.setText(user.getLastName());

        // Find and update the username text view
        TextView usernameView = findViewById(R.id.username);
        String formatUser = "@" + user.getUsername();
        usernameView.setText(formatUser);
    }


    /**
     * Initiates the refresh process for the user's profile.
     * Creates an UpdateProfile instance and attaches the current class as an observer.
     * Adith Iyer
     */
    private void startRefresh() {
        // Create an UpdateProfile instance and attach this class as an observer
        UpdateProfile updateProfile = new UpdateProfile(user);
        updateProfile.attach(this);
    }

    /**
     * Callback interface for handling post loading events
     * Implementations of this interface can be used to perform actions when posts are loaded
     */
    private PostLoadCallback postLoadCallback = new PostLoadCallback() {
        @Override
        public void onPostLoaded(Post post) {
            // This method can be overridden to perform actions when a post is loaded.
            // Leave empty for now as no specific actions are required in this context.
        }
    };


    /**
     * Retrieves posts from FireStore and creates the list of posts to send to update UI with
     * @param tab (Which tab you are on)
     * Raj Nitin Gar
     */
    private void getPosts(int tab){
        //Clear the layout before populating
        ScrollView scrollView1 = findViewById(R.id.scrollView1);
        LinearLayout scrollViewChildLayout = (LinearLayout) scrollView1.getChildAt(0);
        scrollViewChildLayout.removeAllViews();
        Log.d("ProfileViewer tablayoutlistner", "current tab is " + currentTab);
        currentTab = tab;

        List<Post> returnPostsList = new ArrayList<Post>();
        // Query Firebase for posts by that user
        Query postsQuery = db.collection("posts").whereEqualTo("author", userID);
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


    /**
     * Update the correct scrollview with the posts
     * @param posts (list of posts)
     * Adith Iyer
     * Raj Nitin Gar
     */
    private void updateUIWithPosts(List<Post> posts) {
        // Determine which ScrollView to use
        ScrollView scrollView1 = findViewById(R.id.scrollView1);

        // Check if the current tab is the PostViewing tab
        if (currentTab == 0) {
            for(Post post: posts){
                addPostToLayout(post, scrollView1);
            }
        }
        else if (currentTab == 1) {
            showAuthorFollowers(userID, scrollView1);
        }
    }


    /**
     * Adds Posts to the ScrollView Layout
     * @param post
     * @param layout
     * Adith Iyer
     */
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

    /**
     * Method for displaying the profile's followers
     * @param authorID
     * @param followerScrollView
     * Adith Iyer
     */
    private void showAuthorFollowers(String authorID, ScrollView followerScrollView) {
        this.user.getFollowing(data -> {
            List<String> followers = (List<String>) data;

            if (followers != null) {
                // Create a LinearLayout to contain all follower layouts
                LinearLayout followerContainer = new LinearLayout(ProfileViewer.this);
                followerContainer.setOrientation(LinearLayout.VERTICAL);

                for (String followerID : followers) {
                    // Fetch follower details
                    db.collection("users").document(followerID).get()
                            .addOnSuccessListener(document -> {
                                // New layout to display follower details
                                LinearLayout followerLayout = new LinearLayout(ProfileViewer.this);
                                followerLayout.setOrientation(LinearLayout.VERTICAL);

                                // For simplicity and readability, extract only name and last name
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");

                                // Display follower details in a TextView
                                TextView followerTextView = new TextView(ProfileViewer.this);
                                followerTextView.setText(firstName + " " + lastName);
                                followerTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                // Set properties for text
                                followerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Increase text size
                                followerTextView.setTypeface(followerTextView.getTypeface(), Typeface.BOLD); // Make text bold
                                // Add space between follower details
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 16, 0, 16); // Add margins for readability
                                followerLayout.addView(followerTextView, params);
                                // Important for opening ProfileViewer with the new user as the subject
                                // OnClickListener for the follower
                                followerLayout.setOnClickListener(v -> openProfileViewer(followerID));
                                // Add the followerLayout to the container
                                followerContainer.addView(followerLayout);
                                // Add space between follower layouts
                                LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 16); // Space height
                                followerContainer.addView(new Space(ProfileViewer.this), spaceParams);
                            })
                            .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to retrieve follower details.", Toast.LENGTH_SHORT).show());
                }
                // Add the LinearLayout to ScrollView
                followerScrollView.removeAllViews();
                followerScrollView.addView(followerContainer);
            }
        });
    }

    /**
     * Intent to view profile of the clicked user
     * @param userID
     */
    private void openProfileViewer(String userID) {
        Intent profileViewerIntent = new Intent(ProfileViewer.this, ProfileViewer.class);
        profileViewerIntent.putExtra("authorID", userID);
        startActivity(profileViewerIntent);
    }


    /**
     * Intent back to PostView
     * @param post
     * Noah Vendrig
     */
    private void onItemClick(Post post) {
        Intent postViewIntent = new Intent(ProfileViewer.this, PostViewActivity.class);
        postViewIntent.putExtra("post", post);
        startActivity(postViewIntent);
    }

    /**
     * Method to follow the profile's Author
     * @param userID
     * Adith Iyer
     */
    private void followAuthor(String userID) {
        Log.d(TAG, "following starting");
        // Search for the profile of the logged-in user
        db.collection("users").document(loggedInUserID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> following = (List<String>) documentSnapshot.get("following");
                    if (following == null) {
                        following = new ArrayList<>();
                    }
                    // cant follow yourself
                    if (userID.equals(loggedInUserID)){
                        Toast.makeText(ProfileViewer.this, "You cant follow yourself.", Toast.LENGTH_SHORT).show();
                    }
                    // Check to see if not already following
                    else if (!following.contains(userID)) {
                        // If not, add it to the list
                        following.add(userID);
                        Log.d(TAG, "followed");
                        db.collection("users").document(loggedInUserID).update("following", following)
                                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileViewer.this, "You are now following this user.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to follow user.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(ProfileViewer.this, "You are already following this user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileViewer.this, "Failed to retrieve user profile.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Updates Profile Picture
     * Adith Iyer
     */
    private void updateProfilePic() {
        // If the profile picture bitmap is null, check local file or download it
        if (user.getPfpBitmap() == null) {
            if (user.getLocalPfpFile().exists()) {
                // Load and update from local file
                updateProfileImageView(BitmapFactory.decodeFile(user.getLocalPfpFile().getAbsolutePath()));
            }
            // Download and update profile picture asynchronously
            user.dlProfilePicBitmap(this.getApplicationContext(), new User.PfpLoadedCallback() {
                @Override
                public void onPfpLoaded(Bitmap bitmap) {
                    updateProfileImageView(bitmap);
                }
                @Override
                public void onPfpLoadFailed(Exception e) {
                    Log.e(TAG, "Error with loading Profile Picture");
                }
            });
        } else {
            // Update from existing bitmap
            updateProfileImageView(user.getPfpBitmap());
        }
    }


    /**
     * Updates the profile ImageView
     * @param immutableBitmap The original immutable bitmap to be used for the profile picture
     * Adith Iyer
     */
    private void updateProfileImageView(Bitmap immutableBitmap) {

//        null check for bitmap, temp solution. untested app behaviour
        if (immutableBitmap == null) {
            return;
        }

        // Create a mutable copy of the bitmap
        Bitmap pfpImageBitmap = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Set up the canvas and paint for drawing
        Canvas canvas = new Canvas(pfpImageBitmap);
        Paint paint = new Paint();

        // Set paint color to 50% opacity grey
        paint.setColor(Color.parseColor("#00ffffff")); // 50% opacity grey

        // Draw a semi-transparent rectangle over the entire bitmap
        canvas.drawRect(0, 0, pfpImageBitmap.getWidth(), pfpImageBitmap.getHeight(), paint);

        // Draw the original bitmap over the semi-transparent rectangle
        canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);

        // Find the profile ImageView element
        ShapeableImageView profileImg = findViewById(R.id.activity_home_feed_sv_profile);

        // Set the updated bitmap to the ImageView
        profileImg.setImageBitmap(pfpImageBitmap);
    }


    /**
     * Updates when the subject notifies the observer
     * Noah Vendrig
     */
    @Override
    public <T> void update(T newUser) {
        this.user = (User) newUser;
        updateUserTexts(); // update name text
        getPosts(currentTab);
    }
}
