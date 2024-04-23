package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        populateFeed();

        ImageButton profilePicIb = findViewById(R.id.activity_home_feed_ib_profile);

        profilePicIb.setOnClickListener(v -> {
            Log.d(TAG, "profile pib ib clicked");
        });
        createProfilePic();

        Button createPostBt = (Button) findViewById(R.id.activity_home_feed_bt_create_post);
        Log.d(TAG, "Clicked createPost bt");
        createPostBt.setOnClickListener(v -> {
            startActivity(new Intent(HomeFeed.this, CreatePost.class));
        });

        Button followingFeedBt = (Button) findViewById(R.id.activity_home_feed_bt_following_feed);
        followingFeedBt.setOnClickListener(v -> {
            startActivity(new Intent(HomeFeed.this, FollowingFeed.class));
        });
    }

    /***
     * populates FollowingFeed with relevant posts
     */
    private void populateFeed() {
        // query  posts from database

        // dynamically add children to the linear layout ("@+id/activity_home_feed_ll_posts")
        getPosts(new OnPostsLoadedListener() {
            @Override
            public void onPostsLoaded(List<Post> loadedPosts) {
                Log.d(TAG, String.valueOf(loadedPosts.size()));

                LinearLayout linearLayout = findViewById(R.id.activity_home_feed_ll_posts);

                for (Post post : loadedPosts) {
                    // Inflate the post thumbnail layout
                    View postThumbnail = getLayoutInflater().inflate(R.layout.activity_home_feed_post_thumbnail, null);

                    // Populate the post thumbnail with post data

//                    Button likeButton = postThumbnail.findViewById(R.id.like_button);
//                    Button commentButton = postThumbnail.findViewById(R.id.comment_button);
//                    Button shareButton = postThumbnail.findViewById(R.id.share_button);
                    //
                    TextView titleTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_title);
                    TextView authorTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_author);
//                    TextView urlTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_url);
                    TextView dateTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_date);
//                    TextView publisherTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_publisher);
                    TextView bodyTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_summary);


                    titleTv.setText(post.getTitle());
                    authorTv.setText(post.getAuthorName());
//                    urlTv.setText(post.getSourceURL());
                    dateTv.setText(post.getFormattedDateTime());
//                    publisherTv.setText(post.getPublisher());
                    bodyTv.setText(post.getBody());
//                    Log.d(TAG+"post body", post.getBody());


                    // Set onClickListeners for buttons if needed

                    // Add the post thumbnail to the LinearLayout
                    linearLayout.addView(postThumbnail);
                }
            }
        });
    }

    private void getPosts(final OnPostsLoadedListener listener) {
        List<Post> posts = new ArrayList<Post>();
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
                        } else {
                            Log.w(TAG+": Firestore READ error", "Error getting documents in 'posts' collection; ", task.getException());
                        }
                    }
                });
    }

    // Define an interface for the listener
    public interface OnPostsLoadedListener {
        void onPostsLoaded(List<Post> posts);
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