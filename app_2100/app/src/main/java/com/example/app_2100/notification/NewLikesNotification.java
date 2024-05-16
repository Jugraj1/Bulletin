package com.example.app_2100.notification;

import android.app.PendingIntent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.app_2100.Post;

/**
 * Noah Vendrig
 */
public class NewLikesNotification extends Notification {
    private final Post post;
    private int likesDiff;
    private static String TAG = "NewLikesNotification";
    NewLikesNotification(NewLikesNotificationData data){
        super(NotificationType.NEW_LIKES);
        this.post = data.getPost();
        this.likesDiff = data.getlikesDiff();
        construct();
    }
    @Override
    protected void construct() {
        this.notificationBuilder
                .setContentTitle(String.format("%s new likes on your post", likesDiff));
    }

    public Post getPost(){
        return post;
    }
}

