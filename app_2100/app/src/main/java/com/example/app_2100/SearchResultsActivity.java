package com.example.app_2100;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {
    LinearLayout listView;
    ArrayAdapter arrayAdapter;
    private static final String TAG = "SearchResultsActivity_Screen";
    List<Post> posts = new ArrayList<Post>();
    boolean isLoading = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listView = findViewById(R.id.activity_home_feed_lv_posts);

        Bundle extras = getIntent().getExtras();
        String searchFieldString = extras.getString("searchField");
        String dateButtonString = extras.getString("dateButton");
        String dateButtonToString = extras.getString("dateButtonTo");
        assert dateButtonToString != null;
        String[] tokens = dateButtonToString.split(" ");
        int tmToYear = Integer.parseInt(tokens[2]);
        int tmToMonth = getMonth(tokens[0]);
        int tmToDate = Integer.parseInt(tokens[1]);
//        Log.d("year", String.valueOf(tmToYear));
//        Log.d("month", String.valueOf(tmToMonth));
//        Log.d("date", String.valueOf(tmToDate));

        Timestamp tmFrom = new Timestamp(new Date(dateButtonString));
        Timestamp tmTo = new Timestamp(new Date(tmToYear, tmToMonth, tmToDate, 23, 59, 59));
        populateFeed(tmTo, tmFrom);


//        db.collection("posts")
//                .whereLessThan("timeStamp", tmTo)
//                .whereGreaterThan("timeStamp", tmFrom)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Map<String, Object> currData;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                currData = document.getData();
//                                posts.add(new Post(
//                                        document.getId(),
//                                        currData.get("title"),
//                                        currData.get("body"),
//                                        currData.get("author"),
//                                        currData.get("publisher"),
//                                        currData.get("sourceURL"),
//                                        currData.get("timeStamp")
//                                ));
//                            }
//
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//
//                });


    }

    private void getRelevantPosts(final HomeFeed.OnPostsLoadedListener listener, Timestamp tmTo, Timestamp tmFrom) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Log.d("Time to", String.valueOf(tmTo.toDate()));

        db.collection("posts")
                .whereLessThanOrEqualTo("timeStamp", tmTo)
                .whereGreaterThanOrEqualTo("timeStamp", tmFrom)
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
                            listener.onPostsLoaded(posts);
                        } else {
                            Log.w(TAG+": Firestore READ error", "Error getting documents in 'posts' collection; ", task.getException());
                        }
                    }
                });


    }

    private int getMonth(String month) {
        if (month.equals("JAN"))
            return 1;
        if (month.equals("FEB"))
            return 2;
        if (month.equals("MAR"))
            return 3;
        if (month.equals("APR"))
            return 4;
        if (month.equals("MAY"))
            return 5;
        if (month.equals("JUN"))
            return 6;
        if (month.equals("JUL"))
            return 7;
        if (month.equals("AUG"))
            return 8;
        if (month.equals("SEP"))
            return 9;
        if (month.equals("OCT"))
            return 10;
        if (month.equals("NOV"))
            return 11;
        if (month.equals("DEC"))
            return 12;

        return 1;
    }

    private void populateFeed(Timestamp tmTo, Timestamp tmFrom) {
        // query  posts from database

        // dynamically add children to the linear layout ("@+id/activity_home_feed_ll_posts")
        getRelevantPosts(new HomeFeed.OnPostsLoadedListener() {
            @Override
            public void onPostsLoaded(List<Post> loadedPosts) {
                Log.d(TAG, String.valueOf(loadedPosts.size()));

                LinearLayout linearLayout = findViewById(R.id.activity_home_feed_lv_posts);

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
        }, tmTo, tmFrom);
    }




}

