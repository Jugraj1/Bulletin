package com.example.app_2100;

import android.graphics.Bitmap;

import com.example.app_2100.callbacks.FirestoreCallback;
import com.example.app_2100.callbacks.InitialisationCallback;
import com.google.firebase.auth.FirebaseUser;


public class CurrentUser extends User{
    // The current user logged into the app
    private static CurrentUser instance;
    private static String TAG = "CurrentUser";

    private boolean initialised = false;
    private InitialisationCallback initialisationCallback;

    private CurrentUser() { // Private constructor to prevent instantiation from outside
        super(FirebaseAuthConnection.getInstance().getAuth().getCurrentUser().getUid(), new FirestoreCallback() {
            @Override
            public void onUserLoaded(String firstName, String lastName, String pfpLink) {
                // do nothing rn i guess.
            }
        });
        FirebaseUser currUser = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
        if (currUser != null){
            this.setUserID(currUser.getUid()); // set current user id according to current logged in firebase user from auth
//            this.queryUserByID(getUserID(), userCallback);

            setPfpLoadedCallback(new PfpLoadedCallback() {
                @Override
                public void onPfpLoaded(Bitmap bitmap) {
                    // Call your initialization callback here if not null
                    if (initCallback != null) {
                        initCallback.onInitialised();
                    }
                }

                @Override
                public void onPfpLoadFailed(Exception e) {
                    // Handle failure if needed
                }
            });
        }
    }

    public static CurrentUser getCurrent() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public void setInitialisationCallback(InitialisationCallback callback) {
        initialisationCallback = callback;
        if (initialised) {
            callback.onInitialised();
        }
    }

    @Override
    public void queryUserByID(String userID, FirestoreCallback callback) {
        super.queryUserByID(userID, new FirestoreCallback() {
            @Override
            public void onUserLoaded(String fName, String lName, String pfpLink) {
                initialised = true;
                if (initialisationCallback != null) {
                    initialisationCallback.onInitialised();
                }
                callback.onUserLoaded(fName, lName, pfpLink);
            }
        });
    }
}

