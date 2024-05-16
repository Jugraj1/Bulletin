package com.example.app_2100.update;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.app_2100.firebase.FirebaseFirestoreConnection;
import com.example.app_2100.Post;
import com.example.app_2100.callbacks.PostLoadCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class UpdatePostView implements Subject<Post>{

    private final ArrayList<Observer> observers;
    private final ScheduledExecutorService executor;
    private final FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
    public static final String TAG = "UpdatePostView";

    private final String currID;

    public UpdatePostView(Post post){
        observers = new ArrayList<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        this.currID = post.getID();

        start();
    }

    private void start() {
        Log.d("UpdateProfile", "start post");
        queryDatabase();
    }

    private void queryDatabase() {
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
                            data.get("url"),
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
