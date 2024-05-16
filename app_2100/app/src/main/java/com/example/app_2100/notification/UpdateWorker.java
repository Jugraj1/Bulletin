package com.example.app_2100.notification;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.app_2100.App;
import com.example.app_2100.MainActivity;
import com.example.app_2100.Post;
import com.example.app_2100.ProfileViewer;
import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.firebase.FirebaseAuthConnection;
import com.example.app_2100.firebase.FirebaseFirestoreConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateWorker extends Worker {
    public static final String TAG = "UpdateWorker";
    private String currID;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private Map<String, Integer> currPostLikesMap;
//    private Map<String, Integer> newPostLikesMap;

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        auth = FirebaseAuthConnection.getAuth();
        db = FirebaseFirestoreConnection.getDb();
        currPostLikesMap = new HashMap<>();
    }

    /**
     * Performs the background task
     * @return Whether the background task was successful or not
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "WORKER IS DOING WORK");
        if (auth.getUid() == null) {
            return Result.failure();
        }
        currID = auth.getUid();
        Map<String, Integer> postsToNotify = detectChangeInLikes();
        Log.d(TAG, postsToNotify.toString());

        for (Map.Entry<String, Integer> entry : postsToNotify.entrySet()) {
            String postID = entry.getKey();
            int diff = entry.getValue();
            Log.d(TAG, "notifying");
            NewLikesNotificationData data = new NewLikesNotificationData(null, diff, NotificationType.NEW_LIKES, null);
            Notification postCreatedNotif = NotificationFactory.createNotification(data);
            MainActivity.getNotificationManager().notify(3, postCreatedNotif.getNotificationBuilder().build()); // create notific
        }
            return Result.success();
    }

        private Map<String, Integer> detectChangeInLikes () {
            Map<String, Integer> newLikes = getPosts();
            Map<String, Integer> postsToNotify = new HashMap<>();
            for (Map.Entry<String, Integer> entry : newLikes.entrySet()) {
                String postID = entry.getKey();
                int newLikeCount = entry.getValue();

                if (currPostLikesMap.containsKey(postID) || currPostLikesMap.isEmpty()) {
                    int currLikeCount = currPostLikesMap.isEmpty() ? 0 : currPostLikesMap.get(postID);
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
            return postsToNotify;
        }

        /**
         * Get the posts belonging to the current user, and the like count corresponding to each post
         * @return Post
         */
        private Map<String, Integer> getPosts () {
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
                } else {
                    Log.e(TAG, "err fetching new likes");
                }
            });

            return newPostLikesMap;
        }
}