package com.example.app_2100.notification;

import android.app.PendingIntent;

import com.example.app_2100.Post;

public class NewLikesNotificationData implements NotificationData {
    private Post post;
    private int likesDiff;
    NotificationType notifType;
    PendingIntent pendingIntent;
    public NewLikesNotificationData(Post post, int likesDiff, NotificationType notifType, PendingIntent pendingIntent) {
        this.post = post;
        this.likesDiff = likesDiff;
        this.notifType = notifType;
        this.pendingIntent = pendingIntent;
    }
    @Override
    public NotificationType getNotificationType(){
        return this.notifType;
    }

    public Post getPost(){
        return post;
    }
    public int getlikesDiff(){
        return likesDiff;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    @Override
    public String toString() {
        return "NewLikesNotificationData{" +
                "postID=" + post.getID() +
                "likesDiff=" + likesDiff +
                '}';
    }
}
