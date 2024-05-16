package com.example.app_2100.update;

import android.util.Log;

import com.example.app_2100.App;
import com.example.app_2100.MainActivity;
import com.example.app_2100.firebase.FirebaseAuthConnection;
import com.example.app_2100.firebase.FirebaseFirestoreConnection;
import com.example.app_2100.Post;
import com.example.app_2100.listeners.DataLoadedListener;
import com.example.app_2100.notification.NewLikesNotificationData;
import com.example.app_2100.notification.Notification;
import com.example.app_2100.notification.NotificationFactory;
import com.example.app_2100.notification.NotificationType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UpdateFeed implements Subject<List<Post>> {

    private final ArrayList<Observer> observers;
    private final ScheduledExecutorService executor;
    private final FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
    private List<Post> currPosts;
    private List<String> currPostIDs;

    private Post currPost;

    // current db info to be compared current profile page info
    private List<Post> newPosts;
    private List<String> newPostIDs;

    private String currID;

    // How often the database should be repeatedly queried
    private final int REFRESH_TIME = 500; // time in milliseconds

    private Boolean notify = false;
    public static final String TAG = "UpdateFeed";

    private Boolean useFollowingCondition;
    private Query currQuery;

//    Map<String, Integer> newPostLikesMap = new HashMap<>();

    public UpdateFeed(List<Post> posts, Boolean useFollowingCondition){
        Log.d(TAG, "created post refresh");
        observers = new ArrayList<>();
        executor = Executors.newSingleThreadScheduledExecutor();

        currPosts = posts;
        this.useFollowingCondition = useFollowingCondition;
        currPostIDs = getPostAuthorIDs(posts);
        currPostLikesMap = new HashMap<>();

        currID = FirebaseAuthConnection.getAuth().getUid();

        start();
    }

    private void start() {
        Log.d("UpdateFeed", "start feed");
//        executor.scheduleAtFixedRate(this::queryDatabase, 2, REFRESH_TIME, TimeUnit.MILLISECONDS); // use method reference since runnable is functional interface
        executor.scheduleAtFixedRate(this::getChange, 2, 15000, TimeUnit.MILLISECONDS); // use method reference since runnable is functional interface
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
    public void notifyAllObservers(List<Post> posts) {
        for (Observer observer : observers) {
            observer.update(posts);
        }
    }

    // Stop executor if not needed
    public void stop() {
        executor.shutdown();
    }

    private void queryDatabase() {
        newPosts = new ArrayList<>();
        if (useFollowingCondition){
            // query for following
//            currQuery = db.collection("posts")
//                    .orderBy("score", Query.Direction.DESCENDING)// descending in like count
                        // filter by following
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
                        currPosts = newPosts;
                        newPosts.clear();

                        if (!currPostIDs.equals(newPostIDs)){
//                            Log.d(TAG, "new: "+newPostIDs.toString());
                            Log.d(TAG, "new: "+newPostIDs.size());
//                            Log.d(TAG, "old: "+currPostIDs.toString());
                            Log.d(TAG, "old: "+currPostIDs.size());
                            notify = true;
                        }

                        if (notify){
                            notify = false;
                            notifyAllObservers(currPosts);

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

    private void getChange(){
        detectChangeInLikes(new DataLoadedListener() {
            @Override
            public void OnDataLoaded(Object postsToNotify) {
                Map<String, Integer> posts = (Map<String, Integer>) postsToNotify;
                Log.d(TAG, "notify: "+posts.toString());

                for (Map.Entry<String, Integer> entry : posts.entrySet()) {
                    String postID = entry.getKey();
                    int diff = entry.getValue();
                    NewLikesNotificationData data = new NewLikesNotificationData(null, diff, NotificationType.NEW_LIKES, null);
                    Notification postCreatedNotif = NotificationFactory.createNotification(data);
                    MainActivity.getNotificationManager().notify(3, postCreatedNotif.getNotificationBuilder().build()); // create notific
                }
            }
        });
    }

    private void detectChangeInLikes (DataLoadedListener listener) {
        getPosts(new DataLoadedListener() {
            @Override
            public void OnDataLoaded(Object newMap) {
                Map<String, Integer> newPostLikesMap = (Map<String, Integer>) newMap;
                Map<String, Integer> newLikes = newPostLikesMap;
//                Log.d(TAG, "currPost likes map in detect: " + currPostLikesMap);
//                Log.d(TAG, "new likes in detect: " + newPostLikesMap);


                Map<String, Integer> postsToNotify = new HashMap<>();
                for (Map.Entry<String, Integer> entry : newLikes.entrySet()) {
                    String postID = entry.getKey();
                    int newLikeCount = entry.getValue();

                    if (currPostLikesMap.containsKey(postID) || currPostLikesMap.isEmpty()) {
                        if (currPostLikesMap.isEmpty()){ continue; }

                        int currLikeCount = currPostLikesMap.get(postID);
                        // like count has changed
                        int diff = newLikeCount - currLikeCount;
                        if (diff >= 2) { // positive change in likes, >= 2 is when we will notify user
                            Log.d(TAG, "Like count for post " + postID + " has changed from "
                                    + currLikeCount + " to " + newLikeCount);
                            // send notification with diff
                            postsToNotify.put(postID, diff);
                        }
                    } else {
                        // New post detected
                        Log.d(TAG, "New post detected with ID: " + postID);
                    }
                }

                currPostLikesMap = newLikes;
                listener.OnDataLoaded(postsToNotify);
            }
        });
    }

    /**
     * Get the posts belonging to the current user, and the like count corresponding to each post
     * @return Post
     */
    private void getPosts (DataLoadedListener listener) {
        Map<String, Integer> newPostLikesMap = new HashMap<>();
        Query postsQuery = db.collection("posts").whereEqualTo("author", currID);
        postsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> currData;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    currData = document.getData();
                    String postId = document.getId();
                    List<String> likesList = currData.containsKey("likes") ? (List<String>) currData.get("likes") : new ArrayList<String>();
                    int likesCount = likesList.size();
                    newPostLikesMap.put(postId, likesCount);
                }
                listener.OnDataLoaded(newPostLikesMap);
            } else {
                Log.e(TAG, "err fetching new likes");
            }
        });
    }
    private Map<String, Integer> currPostLikesMap;
}
