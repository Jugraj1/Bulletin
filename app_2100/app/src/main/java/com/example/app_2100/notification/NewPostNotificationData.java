package com.example.app_2100.notification;

import com.example.app_2100.Post;

public class NewPostNotificationData implements NotificationData {
    private Post post;
    NotificationType notifType;
    public NewPostNotificationData(Post post, NotificationType notifType) {
        this.post = post;
        this.notifType = notifType;
    }
    @Override
    public NotificationType getNotificationType(){
        return this.notifType;
    }

    public Post getPost(){
        return post;
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
