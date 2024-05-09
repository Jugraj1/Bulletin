package com.example.app_2100.notification;

public abstract class Notification {
    private NotificationType notifType = null;
    public Notification(NotificationType notifType){
        this.notifType = notifType;
        setup();
    }

    private void setup() {
        // Do one time processing here
    }

    protected abstract void construct();

}

