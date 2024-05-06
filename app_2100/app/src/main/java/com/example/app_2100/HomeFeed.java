package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.Current;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFeed extends AppCompatActivity implements OnItemClickListener {
    private static final String TAG = "HomeFeed_Screen";

    RecyclerView recyclerView;
    RecyclerViewAdapter recylerViewAdapter;

    List<Post> posts = new ArrayList<Post>();

    boolean isLoading = false;

    CurrentUser currUser;

    private PostLoadCallback postLoadCallback = new PostLoadCallback() {
        @Override
        public void onPostLoaded(Post post) {
            recylerViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);
        recyclerView = findViewById(R.id.activity_home_feed_rv_posts);

        currUser = CurrentUser.getCurrent();
        currUser.setInitialisationCallback(new InitialisationCallback() {
            @Override
            public void onInitialised() {
                createProfilePic();

                ShapeableImageView profilePicIb = findViewById(R.id.activity_home_feed_sv_profile);
                profilePicIb.setOnClickListener(v -> {
                    Log.d(TAG, "profile pib is clicked");// forward to profile viewer
                });
            }
        });

        populateFeed(); // this does all the recycle view stuff

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
                                        currData.get("timeStamp"),
                                        postLoadCallback
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

    @Override
    public void onItemClick(int position) {
//        Intent postViewIntent = new Intent(HomeFeed.this, PostView.class);
        Intent postViewIntent = new Intent(HomeFeed.this, PostViewActivity.class);
        postViewIntent.putExtra("post", posts.get(position));
        Log.d(TAG, posts.get(position).toString());
        startActivity(postViewIntent);
    }

    // initiates RecyclerViewAdapter
    private void initAdapter() {
        recylerViewAdapter = new RecyclerViewAdapter(posts);
        recylerViewAdapter.setOnItemClickListener(this);
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
//        Bitmap squareImageBitmap = createDummyBitmap(200, 200); // get the user profile pic
//        Log.d(TAG, CurrentUser.getCurrent().toString());

        currUser.dlProfilePicBitmap(this.getApplicationContext(), new User.PfpLoadedCallback() {
            @Override
            public void onPfpLoaded(Bitmap bitmap) {
                Log.d("PFP","pfp loaded");
                updateProfileImageView(bitmap);
            }

            @Override
            public void onPfpLoadFailed(Exception e) {

            }
        });


//        File localPfpFile = new File(this.getCacheDir(), "pfp_"+currUser.getUserID()+".jpg");
//        if (localPfpFile.exists()) {
//            // file already exists locally, no need to redownload
//            Log.d(TAG, "File already exists: " + localPfpFile.getAbsolutePath());
//            updateProfileImageView(BitmapFactory.decodeFile(localPfpFile.getAbsolutePath()));
//        } else {
//            Log.d(TAG, "Getting profile pic from Firebase Storage");
//            updateProfileImageView(currUser.getPfpBitmap());
//            currUser.getProfilePicBitmap(this,
//                new User.PfpLoadedCallback() {
//                    @Override
//                    public void onPfpLoaded(Bitmap bitmap) {
//                        updateProfileImageView(bitmap);
//                    }
//                    @Override
//                    public void onPfpLoadFailed(Exception e) {
//                        Log.d(TAG, "pfp load failed");
//                    }
//                }
//            );
//        }
    }

    private void updateProfileImageView(Bitmap immutableBitmap) {
        Bitmap pfpImageBitmap = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(pfpImageBitmap);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#70000000")); // 50% opacity grey
        canvas.drawRect(0, 0, pfpImageBitmap.getWidth(), pfpImageBitmap.getHeight(), paint);
        canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);

        // get the element
        ShapeableImageView profileImg = findViewById(R.id.activity_home_feed_sv_profile);

        profileImg.setImageBitmap(pfpImageBitmap);
    }
}