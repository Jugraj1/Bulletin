package com.example.app_2100.observer;

import android.util.Log;
import android.widget.TextView;

import com.example.app_2100.DataLoadedListener;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.FirestoreCallback;
import com.example.app_2100.R;
import com.example.app_2100.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Refresh implements Subject<User> {

    private final ArrayList<Observer> observers;
    private final FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private ScheduledExecutorService executor;

    User currUser;
    List<String> currFollowing;
    User newUser;
    List<String> newFollowing;

    Boolean notify = false;

    public Refresh(User user) {
        observers = new ArrayList<>();
        db = FirebaseFirestoreConnection.getDb();
        executor = Executors.newSingleThreadScheduledExecutor();
        currUser = user;
        currUser.getFollowing(new DataLoadedListener() {
            @Override
            public void OnDataLoaded(Object followingList) {
                currFollowing = (List<String>) followingList;
            }
        });
        start();
    }

    private void start() {
        Log.d("Refresh", "start");
        executor.scheduleAtFixedRate(this::queryDatabase, 0, 5, TimeUnit.SECONDS); // use method reference since runnable is functional interface
    }

    private void queryDatabase() {
//        Log.d("Refresh", "currUser: " + currUser.toString());
        newUser = new User(currUser.getUserID(), new FirestoreCallback() {
            @Override
            public void onUserLoaded(String fName, String lName, String pfpStorageLink) {
                if (!fName.equals(currUser.getFirstName()) || !lName.equals(currUser.getLastName())){ // id never changes, only first/last name
                    notify = true;
                }

                newUser.getFollowing(new DataLoadedListener() {
                    @Override
                    public void OnDataLoaded(Object followingList) {
                        newFollowing = (List<String>) followingList;

                        Log.d("Refresh", "currFollowing: "+currFollowing.toString());

                        if (!currFollowing.equals(newFollowing)){
                            Log.d("Refresh", "newFollowing: "+newFollowing.toString());
                            notify = true;
                        }

//                        Log.d("Refresh", "new user : " + newUser.toString());
                        if (notify){
                            notifyAllObservers(newUser);
                            notify = false;
                            currUser = newUser;
                            currFollowing = newFollowing;
                        }
                    }
                });
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

    @Override
    public void notifyAllObservers(User user) {
        for (Observer observer : observers) {
            observer.update(user);
        }

    }

    // Stop the scheduled executor service when no longer needed
    public void stop() {
        executor.shutdown();
    }
}
