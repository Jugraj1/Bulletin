package com.example.app_2100;

import java.util.ArrayList;

public class Database {
    private static Database dbInstance = null;
    private static ArrayList<String> itemsList = new ArrayList<>();
    //TODO implement observer
//    private static ArrayList<Observer> observers = new ArrayList<>();

    private Database(){

    }

    public static Database getInstance(){
        if (dbInstance == null){
            dbInstance = new Database();
        }

        return dbInstance;
    }

    public ArrayList<String> getItemsList() {return itemsList;}
}
// private final ArrayList<String> listofstuff = Database.getInstance().getItemsList()