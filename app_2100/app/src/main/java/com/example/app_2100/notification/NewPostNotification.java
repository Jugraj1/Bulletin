package com.example.app_2100.notification;

public class NewPostNotification extends Notification {
    NewPostNotification(){
        super(NotificationType.NEW_POST);
        construct();
    }
    @Override
    protected void construct() {

    }
}
