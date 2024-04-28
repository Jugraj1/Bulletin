package com.example.app_2100;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecylerViewAdapter recylerViewAdapter;
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
        recyclerView = findViewById(R.id.activity_search_results_rv_posts);

        Bundle extras = getIntent().getExtras();
        String searchFieldString = extras.getString("searchField");
        String dateButtonString = extras.getString("dateButton");
        String dateButtonToString = extras.getString("dateButtonTo");

        Timestamp tmFrom = new Timestamp(new Date(dateButtonString));
        Timestamp tmTo = new Timestamp(new Date(dateButtonToString));
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

        db.collection("posts")
                .whereLessThan("timeStamp", tmTo)
                .whereGreaterThan("timeStamp", tmFrom)
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
                            initAdapter();
                        } else {
                            Log.w(TAG+": Firestore READ error", "Error getting documents in 'posts' collection; ", task.getException());
                        }
                    }
                });


    }

    private void populateFeed(Timestamp tmTo, Timestamp tmFrom) {
        getRelevantPosts(loadedPosts -> {
            posts.addAll(loadedPosts);}, tmTo, tmFrom);
    }

    private void initAdapter() {
        recylerViewAdapter = new RecylerViewAdapter(posts);
        recyclerView.setAdapter(recylerViewAdapter);
    }




}

