package com.example.app_2100;

import android.graphics.Bitmap;

import com.example.app_2100.callbacks.FirestoreCallback;
import com.example.app_2100.callbacks.InitialisationCallback;
import com.example.app_2100.firebase.FirebaseAuthConnection;
import com.google.firebase.auth.FirebaseUser;

/**
 * Noah Vendrig
 */
public class CurrentUser extends User{
    // The current user logged into the app
    private static CurrentUser instance;
    private static String TAG = "CurrentUser";

    private boolean initialised = false;
    private InitialisationCallback initialisationCallback;

    /**
     * Represents the current user of the application
     */
    private CurrentUser() { // Private constructor to prevent instantiation from outside
        // Initialize the CurrentUser object with the UID of the current Firebase user
        super(FirebaseAuthConnection.getInstance().getAuth().getCurrentUser().getUid(), (firstName, lastName, username, pfpLink) -> {});

        FirebaseUser currUser = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
        if (currUser != null){
            // Set the user ID according to the currently logged in Firebase user
            this.setUserID(currUser.getUid());

            // Set a callback for when the profile picture is loaded successfully or failed to load
            setPfpLoadedCallback(new PfpLoadedCallback() {
                @Override
                public void onPfpLoaded(Bitmap bitmap) {
                    // Call Initialization callback if not null when profile picture loaded
                    if (initCallback != null) {
                        initCallback.onInitialised();
                    }
                }

                @Override
                public void onPfpLoadFailed(Exception e) {
                    // Handle failure if needed when profile picture failed to load
                }
            });
        }
    }

    /**
     * Retrieves the instance of the CurrentUser
     * @return The instance of the CurrentUser
     */
    public static CurrentUser getCurrent() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    /**
     * Sets the callback to be executed upon initialization of the CurrentUser
     * @param callback The callback to be executed upon initialization
     */
    public void setInitialisationCallback(InitialisationCallback callback) {
        initialisationCallback = callback;
        // Call the callback immediately if the user is already initialized
        if (initialised) {
            callback.onInitialised();
        }
    }

    /**
     * Queries user information by user ID from Firestore
     * @param userID The user ID to query
     * @param callback The callback to be executed upon user information retrieval
     */
    @Override
    public void queryUserByID(String userID, FirestoreCallback callback) {
        super.queryUserByID(userID, new FirestoreCallback() {
            @Override
            public void onUserLoaded(String fName, String lName, String username, String pfpLink) {
                initialised = true;
                // Call initialization callback if not null
                if (initialisationCallback != null) {
                    initialisationCallback.onInitialised();
                }
                callback.onUserLoaded(fName, lName, username, pfpLink);
            }
        });
    }

}
