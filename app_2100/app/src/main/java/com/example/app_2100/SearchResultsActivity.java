package com.example.app_2100;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.app_2100.callbacks.FirestoreCallback;
import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.listeners.OnPostsLoadedListener;
import com.example.app_2100.search.AVLTree;
import com.example.app_2100.search.FieldIndex;
import com.example.app_2100.search.SearchUtils;
import com.example.app_2100.search.Tree;
import com.example.app_2100.search.parser.Parser;
import com.example.app_2100.search.tokenizer.Tokenizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Jugraj Singh
 * Jinzheng
 */

public class SearchResultsActivity extends AppCompatActivity {
    LinearLayout listView;
    Button load;
    TextView displayInfo;
    TextView searchedAuthor;
    String searchedAuthorUN;

    LinearLayout profileView;
    User searchedUser;

    private PostLoadCallback postLoadCallback = new PostLoadCallback() {
        @Override
        public void onPostLoaded(Post post) {

        }
    };
    private static final String TAG = "SearchResultsActivity_Screen";
    List<Post> posts = new ArrayList<Post>();

    String queryTitle;
    Tree<FieldIndex<Double, String>> simTree;
    HashMap<String, Post> postMap = new HashMap<>();

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
        displayInfo = (TextView) findViewById(R.id.auth);
        Button searchBt = findViewById(R.id.activity_search_results_page_bt_search);
        searchBt.setOnClickListener(v -> {
            startActivity(new Intent(SearchResultsActivity.this, SearchActivity.class));
        });

        Bundle extras = getIntent().getExtras();
        String searchFieldString = extras.getString("searchField");

        // If grammar rules are followed correctly, search results are shown
        // otherwise nothing can really be displayed
//        queryTitle = parseTitle(searchFieldString);

        searchedAuthor = findViewById(R.id.activity_search_results_tv_searchedAuthorID);

        load = findViewById(R.id.activity_searchResults_btn_more);
        try {
            queryTitle = parseTitle(searchFieldString);
            String dateButtonString = extras.getString("dateButton");
            String dateButtonToString = extras.getString("dateButtonTo");
            assert dateButtonToString != null;
            String[] tokens = dateButtonToString.split(" ");
            int tmToYear = Integer.parseInt(tokens[2]);
            int tmToMonth = getMonth(tokens[0]);
            int tmToDate = Integer.parseInt(tokens[1]);

            Timestamp tmFrom = new Timestamp(new Date(dateButtonString));
            Timestamp tmTo = new Timestamp(new Date(tmToYear, tmToMonth, tmToDate, 23, 59, 59));
            populateFeed(tmTo, tmFrom);
            displayInfo.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            // Catching a more general exception
            displayInfo.setText("Invalid input");
            displayInfo.setVisibility(View.VISIBLE);
            load.setVisibility(View.INVISIBLE);
        }

        try {
            String searchedUserName = parseAuthor(searchFieldString);
            db.collection("users")
                    .whereEqualTo("username", searchedUserName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) { // todo change this later to ensure only 1 record for user (or we cna imply it from db rules>?)
                                    Map<String, Object> userData = document.getData();
                                    String fName = (String) userData.get("firstName");
                                    String lName = (String) userData.get("lastName");
                                    String uName = (String) userData.get("username");
                                    String pfpLink = (String) userData.get("pfpStorageLink");

                                    searchedUser = new User(document.getId(), new FirestoreCallback() {
                                        @Override
                                        public void onUserLoaded(String fName, String lName, String username, String empty) {
                                            Log.d("ProfileViewer", "User loaded: " + fName + " " + lName);

                                            // Set the text for firstname, lastname
                                            if (searchedUser.getPfpBitmap() == null){
                                                if (searchedUser.getLocalPfpFile().exists()){
                                                    Bitmap immutableBitmap = BitmapFactory.decodeFile(searchedUser.getLocalPfpFile().getAbsolutePath());
                                                    Bitmap pfpImageBitmap = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                                    Canvas canvas = new Canvas(pfpImageBitmap);
                                                    Paint paint = new Paint();
                                                    paint.setColor(Color.parseColor("#00ffffff")); // 50% opacity grey
                                                    canvas.drawRect(0, 0, pfpImageBitmap.getWidth(), pfpImageBitmap.getHeight(), paint);
                                                    canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);
                                                    // get the element
                                                    ShapeableImageView profileImg = findViewById(R.id.activity_home_feed_sv_searchedAuthorProfile);
                                                    profileImg.setImageBitmap(pfpImageBitmap);
                                                } else {
                                                    Drawable vectorDrawable = VectorDrawableCompat.create(App.getContext().getResources(), R.drawable.baseline_person_24, null);
                                                    searchedUser.setPfpBitmap( App.drawableToBitmap(vectorDrawable));

                                                }
                                                searchedUser.dlProfilePicBitmap(getApplicationContext(), new User.PfpLoadedCallback() {
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
                                                updateProfileImageView(searchedUser.getPfpBitmap());
                                            }
                                        }
                                    });
                                    searchedUser.setUserID(document.getId());

                                    searchedAuthor.setText(searchedUserName);
                                }


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }

                    });

            profileView = findViewById(R.id.activity_search_results_ll_profile);

            profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (searchedUser != null) {
                        Intent profilePage = new Intent(getApplicationContext(), ProfileViewer.class);
                        profilePage.putExtra("authorID", searchedUser.getUserID());
                        startActivity(profilePage);
                    }
                }
            });

            displayInfo.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            searchedAuthor.setText("Author name not provided");
        }

        load.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // add to posts
                int topN = 3;
                final OnPostsLoadedListener listener = new OnPostsLoadedListener() {
                    @Override
                    public void onPostsLoaded(List<Post> loadedPosts) {
                        Log.d(TAG, String.valueOf(loadedPosts.size()));

                        LinearLayout linearLayout = findViewById(R.id.activity_home_feed_lv_posts);


                        for (Post post : loadedPosts) {
                            // Inflate the post thumbnail layout
                            View postThumbnail = getLayoutInflater().inflate(R.layout.activity_home_feed_post_thumbnail, null);

                            // Populate the post thumbnail with post data

                            TextView titleTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_title);
                            TextView authorTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_author);
                            TextView dateTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_date);
                            TextView bodyTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_summary);


                            titleTv.setText(post.getTitle());
                            authorTv.setText(post.getAuthorName());
                            dateTv.setText(post.getFormattedDateTime());
                            bodyTv.setText(post.getBody());

                            // handle likes
                            Button likeBt = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_bt_like);

                            if (post.getLikedByCurrUser()) // user has liked the post
                            {
                                likeBt.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
                            } else { // user hasnt liked the post yet
                                likeBt.setBackground(null);
                            }

                            // Set onClickListeners for buttons if needed
                            likeBt.setOnClickListener(v -> {
                                Log.d(TAG, "toggling like");
                                if (post.getLikedByCurrUser()) // user has liked the post, now wants to unlike
                                {
                                    likeBt.setBackground(null);
                                } else { // user hasnt liked the post yet, and wants to like
                                    likeBt.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
                                }
                                post.toggleLike(CurrentUser.getCurrent().getUserID());
                            });

                            // Add the post thumbnail to the LinearLayout
                            linearLayout.addView(postThumbnail);
                        }
                    }
                };
                List<Post> topNPosts = new ArrayList<>();
                if (!(simTree instanceof AVLTree.EmptyAVL)) {
                    topNPosts = retrieveTopNPostsFromTree(topN);
                    listener.onPostsLoaded(topNPosts);
                }
                if (simTree instanceof AVLTree.EmptyAVL) {
                    load.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void updateProfileImageView(Bitmap immutableBitmap) {
        Bitmap pfpImageBitmap = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(pfpImageBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#00ffffff")); // 50% opacity grey
        canvas.drawRect(0, 0, pfpImageBitmap.getWidth(), pfpImageBitmap.getHeight(), paint);
        canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);
        // get the element
        ShapeableImageView profileImg = findViewById(R.id.activity_home_feed_sv_searchedAuthorProfile);
        profileImg.setImageBitmap(pfpImageBitmap);
    }


    private void onItemClick(Post post) {
        Intent postViewIntent = new Intent(SearchResultsActivity.this, PostViewActivity.class);
        postViewIntent.putExtra("post", post);
        startActivity(postViewIntent);
    }


    private void getRelevantPosts(final OnPostsLoadedListener listener, Timestamp tmTo, Timestamp tmFrom) {

//        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                                Post curPost = new Post(
                                        document.getId(),
                                        currData.get("title"),
                                        currData.get("body"),
                                        currData.get("author"),
                                        currData.get("publisher"),
                                        currData.get("sourceURL"),
                                        currData.get("timeStamp"),
                                        postLoadCallback
                                );


                                double titleSimilarity = SearchUtils.getTextsSimilarity((String) currData.get("title"), queryTitle);
                                FieldIndex<Double, String> simIndex = new FieldIndex<Double, String>(titleSimilarity, document.getId());

                                if (simTree == null) {
                                    simTree = new AVLTree<FieldIndex<Double, String>>(simIndex);
                                } else {
                                    simTree = SearchUtils.insertFieldIndex((AVLTree<FieldIndex<Double, String>>) simTree, simIndex);
                                }
                                postMap.put(curPost.getID(), curPost);

                            }

                            int topN = 3;
                            List<Post> topNPosts = retrieveTopNPostsFromTree(topN);
                            posts.addAll(topNPosts);

                            listener.onPostsLoaded(posts);
                        } else {
                            Log.w(TAG+": Firestore READ error", "Error getting documents in 'posts' collection; ", task.getException());
                        }
                    }
                });



    }

    private List<Post> retrieveTopNPostsFromTree(int topN) {
        List<Post> topNPosts = new ArrayList<Post>();

        if (simTree == null) {
            displayInfo.setText("No results found");
            displayInfo.setVisibility(View.VISIBLE);
            return new ArrayList<>();
        }
        HashSet<String> mostRel =  simTree.max().getIndices();
        for (int curNumPosts = 0; curNumPosts < topN; curNumPosts++) {
            for (Iterator<String> iterator = mostRel.iterator(); curNumPosts < topN && iterator.hasNext(); ) {
                String id = iterator.next();
                Post curPost = postMap.get(id);
                topNPosts.add(curPost);
                if (!iterator.hasNext()) {
                    if ((SearchUtils.getIndexSizeFromTreeRec((AVLTree<FieldIndex<Double, String>>) simTree)) == 1) {
                        simTree = new AVLTree.EmptyAVL<>();
                        return topNPosts;
                    }
                    else {
                        simTree = (AVLTree<FieldIndex<Double, String>>) simTree.delete(simTree.max());
                    }
                } else {
                    curNumPosts++;
                    iterator.remove();
                }
            }

            mostRel = simTree.max().getIndices();
        }
        return topNPosts;
    }

    private String parseTitle(String input) throws Parser.IllegalProductionException {
        Tokenizer tokenizer = new Tokenizer(input);
        Parser parser = new Parser(tokenizer);
        return parser.getTitle();
    }

    private String parseAuthor(String input) throws Parser.IllegalProductionException {
        Tokenizer tokenizer = new Tokenizer(input);
        Parser parser = new Parser(tokenizer);
        return parser.getAuthor();
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

        getRelevantPosts(new OnPostsLoadedListener() {
            @Override
            public void onPostsLoaded(List<Post> loadedPosts) {
                Log.d(TAG, String.valueOf(loadedPosts.size()));

                LinearLayout linearLayout = findViewById(R.id.activity_home_feed_lv_posts);


                for (Post post : loadedPosts) {
                    // Inflate the post thumbnail layout
                    View postThumbnail = getLayoutInflater().inflate(R.layout.activity_home_feed_post_thumbnail, null);
                    TextView titleTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_title);
                    TextView authorTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_author);
                    TextView dateTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_date);
                    TextView bodyTv = postThumbnail.findViewById(R.id.activity_home_feed_post_thumbnail_tv_summary);


                    titleTv.setText(post.getTitle());
                    authorTv.setText(post.getAuthorName());
                    dateTv.setText(post.getFormattedDateTime());
                    bodyTv.setText(post.getBody());

                    // Set onClickListeners for buttons if needed
                    postThumbnail.setOnClickListener(view -> onItemClick(post));
                    // Add the post thumbnail to the LinearLayout
                    linearLayout.addView(postThumbnail);
                }
            }
        }, tmTo, tmFrom);
    }

}

