package com.example.app_2100.notification;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.example.app_2100.App;
import com.example.app_2100.R;

public abstract class Notification {
    // uses:
    // https://www.gofpattern.com/creational/patterns/factoryMethodPattern-java-code.php
    private NotificationType notifType = null;

    protected NotificationCompat.Builder notificationBuilder; // allow child class to access
    public Notification(NotificationType notifType){
        this.notifType = notifType;
        setup(App.getContext());
    }

    private void setup(Context context) {

        String CHANNEL_ID = "channel_1";// The id of the channel.
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle("title")
//                .setContentText("message")
//                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return this.notificationBuilder;
    }

    protected abstract void construct();

}

