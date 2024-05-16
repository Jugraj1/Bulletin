package com.example.app_2100;


import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.app_2100.search.AVLTree;
import com.example.app_2100.search.FieldIndex;
import com.example.app_2100.search.SearchUtils;
import com.example.app_2100.search.Tree;
import com.example.app_2100.search.parser.Parser;
import com.example.app_2100.search.tokenizer.Tokenizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {
    LinearLayout listView;
    //    ArrayAdapter arrayAdapter;
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
    //    List<Post> topNPosts = new ArrayList<>();
//    boolean isLoading = false;

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
//        searchedAuthorUN = (String) searchedAuthor.getText();







//        Log.d("Printing user", searchedUser.getFirstName());

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
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> currData;
                                searchedAuthor.setText(searchedUserName+ ": User cannot be found");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    currData = document.getData();
//                                posts.add(new Post(
//                                        document.getId(),
//                                        currData.get("title"),
//                                        currData.get("body"),
//                                        currData.get("author"),
//                                        currData.get("publisher"),
//                                        currData.get("sourceURL"),
//                                        currData.get("timeStamp")
//                                ));

                                    searchedUser = new User(
                                            document.getId(),
                                            (String) currData.get("firstName"),
                                            (String) currData.get("lastName"));

//                                Log.d("Query before attempting:", queryTitle);
                                    searchedUser.setUserID(document.getId());

                                    searchedAuthor.setText(searchedUserName);

//                                Log.d("Found user:", searchedUser.getFirstName());
                                }


                            } else {
                                Log.w(TAG+": Firestore READ error", "Error getting documents in 'posts' collection; ", task.getException());
                            }
                        }
                    });



            profileView = findViewById(R.id.activity_search_results_ll_profile);



//        System.out.println("Waiting for 1 second...");
//        try {
//            // Make the current thread sleep for 1 second (1000 milliseconds)
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Done waiting!");

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
//            displayInfo.setText("Invalid input");
//            displayInfo.setVisibility(View.VISIBLE);
//            load.setVisibility(View.INVISIBLE);
        }



        //load more posts

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

                            // Set onClickListeners for buttons if needed

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

//    private void getAuthor(final OnPostsLoadedListener listener) {
//        parseAuthor()
//    }



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
//                                posts.add(new Post(
//                                        document.getId(),
//                                        currData.get("title"),
//                                        currData.get("body"),
//                                        currData.get("author"),
//                                        currData.get("publisher"),
//                                        currData.get("sourceURL"),
//                                        currData.get("timeStamp")
//                                ));

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

//                                Log.d("Query before attempting:", queryTitle);

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

//        int curNumPosts = 0;

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

                    // Add the post thumbnail to the LinearLayout
                    linearLayout.addView(postThumbnail);
                }
            }
        }, tmTo, tmFrom);
    }




}

