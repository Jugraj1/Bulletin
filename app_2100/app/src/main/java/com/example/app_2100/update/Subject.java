package com.example.app_2100.update;

public interface Subject<T> {
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notifyAllObservers(T classifier);
}
