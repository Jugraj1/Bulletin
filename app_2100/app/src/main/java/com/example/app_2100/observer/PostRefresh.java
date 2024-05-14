package com.example.app_2100.observer;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.Post;
import com.example.app_2100.PostLoadCallback;
import com.example.app_2100.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PostRefresh implements Subject<Post>{

    private final ArrayList<Observer> observers;
    private final ScheduledExecutorService executor;
    private final FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
    private Post currPost;
    private Post newPost;
    private final int REFRESH_TIME = 500;
    public static final String TAG = "PostRefresh";

    private String currID;

    public PostRefresh(Post post){
        observers = new ArrayList<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        this.currPost = post;
        this.currID = post.getID();

        start();
    }

    private void start() {
        Log.d("Refresh", "start post");
        queryDatabase();
//        executor.scheduleAtFixedRate(this::queryDatabase, 0, REFRESH_TIME, TimeUnit.MILLISECONDS); // use method reference since runnable is functional interface
    }

    private void queryDatabase() {
//        db.collection("posts")
//                .document(currPost.getID())
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot documentSnapshots) {
//
//                    }
//                });
        final DocumentReference docRef = db.collection("posts").document(currID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Map<String, Object> data = snapshot.getData();
                    new Post(
                            currID,
                            data.get("title"),
                            data.get("body"),
                            data.get("author"),
                            data.get("publisher"),
                            data.get("sourceURL"),
                            data.get("timeStamp"),
                            new PostLoadCallback() {
                                @Override
                                public void onPostLoaded(Post post) {
                                    notifyAllObservers(post);
                                }
                            });

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }


    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    public void stop() {
        executor.shutdown();
    }

    @Override
    public void notifyAllObservers(Post post) {
        for (Observer observer : observers) {
            observer.update(post);
        }
    }

}
