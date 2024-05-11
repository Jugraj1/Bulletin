package com.example.app_2100.notification;

public class NewCommentNotification extends Notification {

    NewCommentNotification(){
        super(NotificationType.NEW_COMMENT);
        construct();
    }
    @Override
    protected void construct() {

    }
}
