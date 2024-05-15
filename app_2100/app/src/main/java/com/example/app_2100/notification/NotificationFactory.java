package com.example.app_2100.notification;

import android.util.Log;

public class NotificationFactory {
    public static Notification createNotification(NotificationData notifData) {
        Notification notification = null;

        switch (notifData.getNotificationType()) {
            case NEW_COMMENT:
                Log.d("NotificationFactory", "comment");
//                assert notifData instanceof NewCommentNotificationData;
//                NewCommentNotificationData newCommentNotifData = (NewCommentNotificationData) notifData;
                notification = new NewCommentNotification();
                break;
            case NEW_POST:
                assert notifData instanceof NewPostNotificationData;
                Log.d("NotificationFactory", "creating NewPost notification");
                NewPostNotificationData newPostNotifData = (NewPostNotificationData) notifData;
                notification = new NewPostNotification(newPostNotifData);
                break;
            default:
                // throw some exception
                Log.d("NotificationFactory", "DEFAULT CASE BAD BAD");
                break;
        }
        return notification;
    }
}
