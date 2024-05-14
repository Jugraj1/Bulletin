package com.example.app_2100.observer;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.app_2100.App;
import com.example.app_2100.DataLoadedListener;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.FirestoreCallback;
import com.example.app_2100.PostLoadCallback;
import com.example.app_2100.User;
import com.example.app_2100.Post;
import com.example.app_2100.HomeFeed;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Refresh implements Subject<User> {

    private final ArrayList<Observer> observers;
    private final ScheduledExecutorService executor;
    private final FirebaseFirestore db = FirebaseFirestoreConnection.getDb();

    // info on current profile page
    private User currUser;
    private List<String> currFollowing;
    private List<Post> currPosts;
    private List<String> currPostIDs;

    private Post currPost;

    // current db info to be compared current profile page info
    private User newUser;
    private List<String> newFollowing;
    private List<Post> newPosts;
    private List<String> newPostIDs;

    private Post newPost;

    // How often the database should be repeatedly queried
    private final int REFRESH_TIME = 500; // time in milliseconds

    private Boolean notify = false;
    public static final String TAG = "Refresh";

    private Boolean useFollowingCondition;
    private Query currQuery;

    public Refresh(List<Post> posts, Boolean useFollowingCondition){
        Log.d(TAG, "created post refresh");
        observers = new ArrayList<>();
        executor = Executors.newSingleThreadScheduledExecutor();

        currPosts = posts;
        this.useFollowingCondition = useFollowingCondition;
        currPostIDs = getPostAuthorIDs(posts);

        startFeed();
    }

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
                        startProfile(); // start once starting info is gathered
                    }
                });
            }
        });
    }

    private void startProfile() {
        Log.d("Refresh", "start profile");
        executor.scheduleAtFixedRate(this::queryDatabaseProfile, 0, REFRESH_TIME, TimeUnit.MILLISECONDS); // use method reference since runnable is functional interface
    }

    private void startFeed() {
        Log.d("Refresh", "start feed");
        executor.scheduleAtFixedRate(this::queryDatabaseFeed, 0, REFRESH_TIME, TimeUnit.MILLISECONDS); // use method reference since runnable is functional interface
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




    private void queryDatabaseFeed() {
        newPosts = new ArrayList<>();
        if (useFollowingCondition){
            // query for following
//            currQuery = db.collection("posts")
//                    .orderBy("score", Query.Direction.DESCENDING)// descending in like count
//                    .limit(App.getBATCH_NUMBER());
        } else{
            currQuery = db.collection("posts")
                    .orderBy("score", Query.Direction.DESCENDING)// descending in like count
                    .limit(App.getBATCH_NUMBER());
        }
        currQuery.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    Map<String, Object> currData;
                    for (QueryDocumentSnapshot document : documentSnapshots) {
                        currData = document.getData();
                        newPosts.add(new Post(
                                document.getId(),
                                currData.get("title"),
                                currData.get("body"),
                                currData.get("author"),
                                currData.get("publisher"),
                                currData.get("sourceURL"),
                                currData.get("timeStamp"),
                                post -> {}
                        ));
                    }

                    newPostIDs = getPostAuthorIDs(newPosts);
                    if (!currPostIDs.equals(newPostIDs)){
                        notify = true;
                    }

                    if (notify){
                        notify = false;
                        notifyAllObservers(newUser);

                        // reset current posts info to the changed values
                        currPostIDs = newPostIDs;
                    }
                }
            });


    }

    private List<String> getPostAuthorIDs(List<Post> posts){
        return posts.stream()
                .map(Post::getID) // Extract the ID of each Post object
                .collect(Collectors.toList());
    }

    private void queryDatabaseProfile() {
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
}
