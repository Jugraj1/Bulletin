package com.example.app_2100.notification;

import android.app.PendingIntent;

import com.example.app_2100.Post;

/**
 * Noah Vendrig
 */
public class NewLikesNotificationData implements NotificationData {
    private Post post;
    private int likesDiff;
    NotificationType notifType;
    public NewLikesNotificationData(Post post, int likesDiff, NotificationType notifType) {
        this.post = post;
        this.likesDiff = likesDiff;
        this.notifType = notifType;
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

    @Override
    public String toString() {
        return "NewLikesNotificationData{" +
                "postID=" + post.getID() +
                "likesDiff=" + likesDiff +
                '}';
    }
}
