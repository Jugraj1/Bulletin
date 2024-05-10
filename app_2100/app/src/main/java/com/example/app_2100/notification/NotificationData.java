package com.example.app_2100.notification;

/***
 * Passed into the notification factory, so the factory can create the desired notification with desired data
 */
public interface NotificationData {
    NotificationType getNotificationType();
    @Override
    String toString();
}

