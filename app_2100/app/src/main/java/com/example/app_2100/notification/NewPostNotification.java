package com.example.app_2100.notification;

import android.app.PendingIntent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.app_2100.Post;

public class NewPostNotification extends Notification {
    private final Post post;
    private final PendingIntent pendingIntent;
    private static String TAG = "NewPostNotification";
    NewPostNotification(NewPostNotificationData data){
        super(NotificationType.NEW_POST);
        this.post = data.getPost();
        this.pendingIntent = data.getPendingIntent();
        construct();
    }
    @Override
    protected void construct() {
        this.notificationBuilder
                .setContentTitle("Tap to view your new post")
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(String.format("Your post: '%s' has been successfully created!", post.getTitle()))
                )
                .setContentIntent(pendingIntent); // go to PostView of the post
//                .setContentText(String.format("Your post: '%s' has been successfully created! \n Tap to view it", "title"));
    }

    public Post getPost(){
        return post;
    }
}
