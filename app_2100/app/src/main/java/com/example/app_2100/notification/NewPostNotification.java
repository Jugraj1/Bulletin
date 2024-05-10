package com.example.app_2100.notification;

import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.app_2100.Post;

public class NewPostNotification extends Notification {
    private final Post post;
    private static String TAG = "NewPostNotification";
    NewPostNotification(NewPostNotificationData data){
        super(NotificationType.NEW_POST);
        this.post = data.getPost();
        construct();
    }
    @Override
    protected void construct() {
        this.notificationBuilder
                .setContentTitle("Tap to view your new post")
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(String.format("Your post: '%s' has been successfully created!", post.getTitle()))
                );
//                .setContentText(String.format("Your post: '%s' has been successfully created! \n Tap to view it", "pakistan"));
    }

    public Post getPost(){
        return post;
    }
}
