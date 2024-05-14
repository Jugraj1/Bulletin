package com.example.app_2100.observer;

import android.util.Log;

import com.example.app_2100.DataLoadedListener;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.FirestoreCallback;
import com.example.app_2100.User;
import com.example.app_2100.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Refresh implements Subject<User> {

    private final ArrayList<Observer> observers;
    private final ScheduledExecutorService executor;

    // info on current profile page
    User currUser;
    List<String> currFollowing;
    List<Post> currPosts;

    // current db info to be compared current profile page info
    User newUser;
    List<String> newFollowing;
    List<Post> newPosts;

    // How often the database should be repeatedly queried
    private final int REFRESH_TIME = 2;

    Boolean notify = false;
    public static final String TAG = "Refresh";

    public Refresh(User user) {
        observers = new ArrayList<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        currUser = user;
        currUser.getFollowing(new DataLoadedListener() {
            @Override
            public void OnDataLoaded(Object followingList) {
                currFollowing = (List<String>) followingList;
                currUser.getPosts(new DataLoadedListener() {
                    @Override
                    public void OnDataLoaded(Object postsList) {
                        currPosts = (List<Post>) postsList;
                        start(); // start once starting info is gathered
                    }
                });
            }
        });
    }

    private void start() {
        Log.d("Refresh", "start");
        executor.scheduleAtFixedRate(this::queryDatabase, 0, REFRESH_TIME, TimeUnit.SECONDS); // use method reference since runnable is functional interface
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
                        newUser.getPosts(new DataLoadedListener() {
                            @Override
                            public void OnDataLoaded(Object postsList) {

                                newPosts = (List<Post>) postsList;

                                if (!currFollowing.equals(newFollowing) || currPosts.size() != newPosts.size()){
                                    notify = true;
                                }

                                // Log.d("Refresh", "new user : " + newUser.toString());
                                if (notify){
//                                    Log.d(TAG, "notifying");
                                    notify = false;
                                    notifyAllObservers(newUser);

                                    // reset current User info to the changed values
                                    currUser = newUser;
                                    currFollowing = newFollowing;
                                    currPosts = newPosts;
                                }
                            }
                        });
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
