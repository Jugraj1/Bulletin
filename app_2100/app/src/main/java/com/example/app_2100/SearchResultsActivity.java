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
                            initAdapter();
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
        getRelevantPosts(loadedPosts -> {
            posts.addAll(loadedPosts);}, tmTo, tmFrom);
    }

    private void initAdapter() {
        recylerViewAdapter = new RecylerViewAdapter(posts);
        recyclerView.setAdapter(recylerViewAdapter);
    }




}

