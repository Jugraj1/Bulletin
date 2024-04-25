package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFeed extends AppCompatActivity {
    private static final String TAG = "HomeFeed_Screen";

    RecyclerView recyclerView;
    RecylerViewAdapter recylerViewAdapter;
    List<Post> posts = new ArrayList<Post>();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);
        recyclerView = findViewById(R.id.activity_home_feed_rv_posts);

        populateFeed(); // this does all the recycle view stuff

        ImageButton profilePicIb = findViewById(R.id.activity_home_feed_ib_profile);
        profilePicIb.setOnClickListener(v -> {
            Log.d(TAG, "profile pib ib clicked");
        });

        createProfilePic();
        Log.d(TAG, User.getCurrent().getUserID());

        Button createPostBt = findViewById(R.id.activity_home_feed_bt_create_post);
        createPostBt.setOnClickListener(v -> {
            startActivity(new Intent(HomeFeed.this, CreatePost.class));
        });

        Button followingFeedBt = findViewById(R.id.activity_home_feed_bt_following_feed);
        followingFeedBt.setOnClickListener(v -> {
            startActivity(new Intent(HomeFeed.this, FollowingFeed.class));
        });

        Button searchBt = findViewById(R.id.activity_home_feed_bt_search);
        searchBt.setOnClickListener(v -> {
            startActivity(new Intent(HomeFeed.this, SearchActivity.class));
        });





    }

    /***
     * populates FollowingFeed with relevant posts
     */
    private void populateFeed() {
        getPosts(loadedPosts -> {
            posts.addAll(loadedPosts);
        });
    }


    private void getPosts(final OnPostsLoadedListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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
                                        currData.get("timeStamp")
                                ));
                            }
                            // call listener with the loaded posts

                            listener.onPostsLoaded(posts);
                            initAdapter(); // put this here so it waits for posts to be queried
                            initScrollListener();
                        } else {
                            Log.w(TAG+": Firestore READ error", "Error getting documents in 'posts' collection; ", task.getException());
                        }
                    }
                });
    }

    // interface for the post loaded listener
    public interface OnPostsLoadedListener {
        void onPostsLoaded(List<Post> posts);
    }

    // initiates RecyclerViewAdapter
    private void initAdapter() {
        recylerViewAdapter = new RecylerViewAdapter(posts);
        recyclerView.setAdapter(recylerViewAdapter);
    }

    // initScrollListener() method is the method where we are checking
    // the scrolled state of the RecyclerView and if bottom-most is visible
    // we are showing the loading view and populating the next list
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
             @Override
             public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                 super.onScrollStateChanged(recyclerView, newState);
             }
             @Override
             public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                 super.onScrolled(recyclerView, dx, dy);

                 LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                 if (!isLoading) {
                     if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == posts.size() - 1) {
                         // bottom of list!
                         loadMore();
                         isLoading = true;
                     }
                 }
             }
        });
    }

    private void loadMore() {
        posts.add(null);
        recylerViewAdapter.notifyItemInserted(posts.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            posts.remove(posts.size() - 1);
            int scrollPosition = posts.size();
            recylerViewAdapter.notifyItemRemoved(scrollPosition);

            populateFeed(); // get more posts - right now this just gets the same list of posts
            isLoading = false;
        }, 0); // delay, we set to 0 for now
    }

    private void createProfilePic(){
        Bitmap squareImageBitmap = createDummyBitmap(200, 200);

        // Combine square image with circular corner drawable
        Bitmap roundedImageBitmap = Bitmap.createBitmap(squareImageBitmap.getWidth(), squareImageBitmap.getHeight(), squareImageBitmap.getConfig());
        Canvas canvas = new Canvas(roundedImageBitmap);
        Paint paint = new Paint();
        Drawable drawable = getResources().getDrawable(R.drawable.circular_corner);
        drawable.setBounds(0, 0, squareImageBitmap.getWidth(), squareImageBitmap.getHeight());
        drawable.draw(canvas);
        canvas.drawBitmap(squareImageBitmap, 0f, 0f, paint);

        // Set rounded image as background of the ImageButton
        ImageButton imageButton = findViewById(R.id.activity_home_feed_ib_profile);
        BitmapDrawable roundedImageDrawable = new BitmapDrawable(getResources(), roundedImageBitmap);
        imageButton.setBackground(roundedImageDrawable);
    }

    private Bitmap createDummyBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLUE);
        return bitmap;
    }
}