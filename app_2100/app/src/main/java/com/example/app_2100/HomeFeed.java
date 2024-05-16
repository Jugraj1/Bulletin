package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.util.Log;

import com.example.app_2100.callbacks.InitialisationCallback;
import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.listeners.OnItemClickListener;
import com.example.app_2100.listeners.OnPostsLoadedListener;
import com.example.app_2100.notification.UpdateWorker;
import com.example.app_2100.update.UpdateFeed;
import com.example.app_2100.update.Observer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFeed extends AppCompatActivity implements OnItemClickListener, Observer {
    private static final String TAG = "HomeFeed_Screen";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recylerViewAdapter;
    private List<Post> posts = new ArrayList<Post>();
    private boolean isLoading = false;
    private CurrentUser currUser;
    private int scrollPosition;

    private int BATCH_NUMBER;

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

        startUpdateWorker();

        BATCH_NUMBER = App.getBATCH_NUMBER();

        recyclerView = findViewById(R.id.activity_home_feed_rv_posts);

        currUser = CurrentUser.getCurrent();
        currUser.setInitialisationCallback(new InitialisationCallback() {
            @Override
            public void onInitialised() {
                createProfilePic();

                ShapeableImageView profilePicIb = findViewById(R.id.activity_home_feed_sv_profile);
                profilePicIb.setOnClickListener(v -> {
                    Log.d(TAG, "profile pib is clicked");// forward to profile viewer
                    Intent profileIntent = new Intent(HomeFeed.this, ProfileViewer.class);
                    profileIntent.putExtra("authorID", CurrentUser.getCurrent().getUserID());
                    startActivity(profileIntent); // go to the ProfileViewer screen for the current user
                });
            }
        });

        // Initialise RecyclerViewAdapter and other processes to display/update the RecyclerView
        initAdapter();
        initScrollListener();
        populateFeed();
        initiateRefresh();

        // Set OnClick for buttons
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

        // Set SwipeRefreshLayout for manual refresh of the page by the user
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.activity_home_feed_srl_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "REFRESH via swipe");
                // Clear existing values and repopulate feed
                posts.clear();
                lastVisible = null;
                initScrollListener();
                populateFeed();

                // Hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void startUpdateWorker(){
//        Constraints constraints = new Constraints.Builder()
//                .setRequiresCharging(false)
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                UpdateWorker.class, 15, TimeUnit.MINUTES)
//                .setConstraints(constraints)
                .build();
//        WorkManager.getInstance(this).enqueue(workRequest);

//        WorkManager.getInstance(this)
//                .getWorkInfoByIdLiveData(workRequest.getId())
//                .observe(this, workInfo -> {
//                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
//                        // Handle the successful completion of the background task
//                    }
//                });
    }

    /**
     * Reset the Posts displayed on the homescreen
     */
    private void reset(){
        posts.clear();
        lastVisible = null;
        initScrollListener();
        populateFeed();
    }

    /***
     * Initiate the object which detects changes in posts, so that the homescreen can be regularly updated
     */
    private void initiateRefresh() {
        // Create a UpdateProfile instance and attach this class as an observer
        UpdateFeed r = new UpdateFeed(posts, false);
        r.attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        posts.clear();
        lastVisible = null;
        initScrollListener();
        populateFeed();
    }

    /***
     * populates FollowingFeed with relevant posts
     */
    private void populateFeed() {
        getPosts(loadedPosts -> {});
    }
    Query query;
    DocumentSnapshot lastVisible = null;

    private void getPosts(final OnPostsLoadedListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // change to score later
        if (lastVisible == null){
            query = db.collection("posts")
                    .orderBy("score", Query.Direction.DESCENDING)// descending in like count
                    .limit(BATCH_NUMBER);
        } else {
            query = db.collection("posts")
                    .orderBy("score", Query.Direction.DESCENDING) // descending in like count
                    .startAfter(lastVisible) // start from prev
                    .limit(BATCH_NUMBER);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // last visible document
                        if (documentSnapshots.size() == 0){
                            return;
                        }
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);
                        // TODO handle when we run out of posts to display (just refresh to top of page or come up with better solution)
                        Map<String, Object> currData;
                        for (QueryDocumentSnapshot document : documentSnapshots) {
                            currData = document.getData();
//                            Log.d(TAG, "Post {" +
//                                    "title='" + currData.get("title") + '\'' +
//                                    ", authorID='" + currData.get("author") + '\'' +
//                                    ", likes=" + currData.get("likes").toString() +
//                                    ", score=" + currData.get("score") +
//                                   ", url='" + currData.get("url") + '\'' +
//                                    ", timeStamp=" + currData.get("timeStamp") +
//                                    '}');
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
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        if (posts == null || posts.size() == 0){
            return;
        }
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
                if (linearLayoutManager!=null){
                    scrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
                }

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == posts.size() - 1) {
                        // bottom of list! load more posts
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
            if (posts.size() - 1 < 0){
                return;
            }
            posts.remove(posts.size() - 1);
            int scrollPosition = posts.size();
            recylerViewAdapter.notifyItemRemoved(scrollPosition);

            populateFeed(); // get more posts - right now this just gets the same list of posts
            isLoading = false;
        }, 0); // delay, we set to 0 for now
    }

    private void createProfilePic(){
        // get the user profile pic
//        Log.d(TAG, CurrentUser.getCurrent().toString());

        if (currUser.getPfpBitmap() == null){
            if (currUser.getLocalPfpFile().exists()){
                updateProfileImageView(BitmapFactory.decodeFile(currUser.getLocalPfpFile().getAbsolutePath()));
            }
            currUser.dlProfilePicBitmap(this.getApplicationContext(), new User.PfpLoadedCallback() {
                @Override
                public void onPfpLoaded(Bitmap bitmap) {
                    updateProfileImageView(bitmap);
                }
                @Override
                public void onPfpLoadFailed(Exception e) {
                    Log.e(TAG, "Error with loading pfp");
                }
            });
        } else {
            updateProfileImageView(currUser.getPfpBitmap());
        }



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

//        paint.setColor(Color.parseColor("#70000000")); // 50% opacity grey for click
        paint.setColor(Color.parseColor("#00ffffff")); // 50% opacity grey
        canvas.drawRect(0, 0, pfpImageBitmap.getWidth(), pfpImageBitmap.getHeight(), paint);
        canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);

        // get the element
        ShapeableImageView profileImg = findViewById(R.id.activity_home_feed_sv_profile);

        profileImg.setImageBitmap(pfpImageBitmap);
    }

    @Override
    public <T> void update(T posts) {
        reset();
    }

}