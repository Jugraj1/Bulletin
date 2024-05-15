package com.example.app_2100.update;

import android.util.Log;

import com.example.app_2100.listeners.DataLoadedListener;
import com.example.app_2100.callbacks.FirestoreCallback;
import com.example.app_2100.User;
import com.example.app_2100.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateProfile implements Subject<User> {
    private final ArrayList<Observer> observers;
    private final ScheduledExecutorService executor;

    // info on current profile page
    private User currUser;
    private List<String> currFollowing;
    private List<Post> currPosts;

    // current db info to be compared current profile page info
    private User newUser;
    private List<String> newFollowing;
    private List<Post> newPosts;

    // How often the database should be repeatedly queried
    private final int REFRESH_TIME = 500; // time in milliseconds
    private Boolean notify = false;
    public static final String TAG = "UpdateProfile";

    public UpdateProfile(User user) {
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
                        startProfile(); // start once starting info is gathered
                    }
                });
            }
        });
    }

    private void startProfile() {
        Log.d("UpdateProfile", "start profile");
        executor.scheduleAtFixedRate(this::queryDatabaseProfile, 2, REFRESH_TIME, TimeUnit.MILLISECONDS); // use method reference since runnable is functional interface
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

    // Stop executor if not needed
    public void stop() {
        executor.shutdown();
    }

    private void queryDatabaseProfile() {
//        Log.d("UpdateProfile", "currUser: " + currUser.toString());
        newUser = new User(currUser.getUserID(), new FirestoreCallback() {
            @Override
            public void onUserLoaded(String fName, String lName, String username, String pfpStorageLink) {
                if(fName != null && lName != null && username != null){
                    if (!fName.equals(currUser.getFirstName()) || !lName.equals(currUser.getLastName()) || !username.equals(currUser.getUsername())){ // id never changes, only first/last name
                        notify = true;
                    }
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

                                // Log.d("UpdateProfile", "new user : " + newUser.toString());
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
}
