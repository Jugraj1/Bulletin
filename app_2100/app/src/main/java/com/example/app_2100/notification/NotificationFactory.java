package com.example.app_2100.notification;

public class NotificationFactory {
    public static Notification createNotification(NotificationType notifType) {
        Notification notification = null;
        switch (notifType) {
            case NEW_COMMENT:
                notification = new NewCommentNotification();
                break;
            case NEW_POST:
                notification = new NewPostNotification();
                break;
            default:
                // throw some exception
                break;
        }
        return notification;
    }
}
