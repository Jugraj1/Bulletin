package com.example.app_2100.notification;

import android.app.PendingIntent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.app_2100.Post;

public class NewLikesNotification extends Notification {
    private final Post post;
    private int likesDiff;
    private final PendingIntent pendingIntent;
    private static String TAG = "NewLikesNotification";
    NewLikesNotification(NewLikesNotificationData data){
        super(NotificationType.NEW_LIKES);
        this.post = data.getPost();
        this.likesDiff = data.getlikesDiff();
        this.pendingIntent = data.getPendingIntent();
        construct();
    }
    @Override
    protected void construct() {
        this.notificationBuilder
                .setContentTitle("Tap to view your new post")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format("%s new likes on your post", likesDiff))
                )
                .setContentIntent(pendingIntent); // go to PostView of the post
//                .setContentText(String.format("Your post: '%s' has been successfully created! \n Tap to view it", "title"));
    }

    public Post getPost(){
        return post;
    }
}

