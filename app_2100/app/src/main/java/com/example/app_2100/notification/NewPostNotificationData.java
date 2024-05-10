package com.example.app_2100.notification;

import android.app.PendingIntent;

import com.example.app_2100.Post;

public class NewPostNotificationData implements NotificationData {
    private Post post;
    NotificationType notifType;
    PendingIntent pendingIntent;
    public NewPostNotificationData(Post post, NotificationType notifType, PendingIntent pendingIntent) {
        this.post = post;
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

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    @Override
    public String toString() {
        return "NewPostNotificationData{" +
                "postAuthorID=" + post.getAuthorID() +
                "postAuthorID=" + post.getAuthorID() +
                ", notifType=" + notifType.toString() +
                '}';
    }
}
