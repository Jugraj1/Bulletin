package com.example.app_2100.notification;

import android.util.Log;

public class NotificationFactory {
    public static Notification createNotification(NotificationData notifData) {
        Notification notification = null;

        switch (notifData.getNotificationType()) {
            case NEW_POST:
                assert notifData instanceof NewPostNotificationData;
                Log.d("NotificationFactory", "creating NewPost notification");
                NewPostNotificationData newPostNotifData = (NewPostNotificationData) notifData;
                notification = new NewPostNotification(newPostNotifData);
                break;
            case NEW_LIKES:
                assert notifData instanceof NewLikesNotificationData;
                Log.d("NotificationFactory", "creating NewLikes notification");
                NewLikesNotificationData newLikesNotifData = (NewLikesNotificationData) notifData;
                notification = new NewLikesNotification(newLikesNotifData);
                break;
            default:
                // throw some exception
                Log.d("NotificationFactory", "DEFAULT CASE BAD BAD");
                break;
        }
        return notification;
    }
}
