package com.example.app_2100.observer;

import com.example.app_2100.User;

public interface Observer {
    public <T> void update(T t);
}
