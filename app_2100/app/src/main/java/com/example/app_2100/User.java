package com.example.app_2100;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.app_2100.callbacks.FirestoreCallback;
import com.example.app_2100.callbacks.InitialisationCallback;
import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.firebase.FirebaseFirestoreConnection;
import com.example.app_2100.listeners.DataLoadedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Noah Vendrig
 */
public class User {
    private String firstName;
    private String lastName;
    private String userID;
    private String pfpStorageLink;

    private String username;
    private String pfpLocalLink;
    private String TAG = "User";
    private final FirebaseFirestore db = FirebaseFirestoreConnection.getDb().getInstance();
    private Boolean isInitialised = false;
    public InitialisationCallback initCallback;

    private Bitmap pfpBitmap;

    private File localPfpFile;
    FirebaseStorage storage;
    StorageReference pfpRef;


    /**
     * Constructor for User class
     * @param userID
     * @param callback
     */
    public User(String userID, FirestoreCallback callback){
        this.userID = userID;

        this.pfpLocalLink = String.format("pfp_%s.jpg", this.userID);
        this.localPfpFile = new File(App.getContext().getCacheDir(), "pfp_"+this.userID+".jpg");
//        this.pfpStorageLink = String.format("gs://app-f4755.appspot.com/pfp/%s.jpg", this.userID);


//        This is the old one. Remove it?
//        this.pfpStorageLink = "gs://app-f4755.appspot.com/pfp/" + this.userID + ".jpg";

        storage = FirebaseStorage.getInstance();
        queryUserByID(this.userID, callback);
        // synchronised
         // file structure is root/pfp/{userId}.jpg
    }

    /***
     *  Used in the UserGenerator (making dummy accounts), we only need basic instance of User
     * @param userID
     * @param firstName
     * @param lastName
     */
    public User(String userID, String firstName, String lastName){
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    /**
     * Get the Users information from the database
     * Download the profile picture from the Firestore storage
     * @param userID The users id to crosscheck with database
     * @param callback The callback to be called when the data is loaded
     */
    public void queryUserByID(String userID, FirestoreCallback callback){
        db.collection("users")
                .whereEqualTo(FieldPath.documentId(), userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) { // todo change this later to ensure only 1 record for user (or we cna imply it from db rules>?)
//                            Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> userData = document.getData();
                                String fName = (String) userData.get("firstName");
                                String lName = (String) userData.get("lastName");
                                String uName = (String) userData.get("username");
                                String pfpLink = (String) userData.get("pfpStorageLink");

//                                If the pfpLink is null, then set it to the default pfp link
                                // Log.d(TAG, "pfpLink: "+ pfpLink); // "gs://app-f4755.appspot.com/pfp/1.png"
                                
                                firstName = fName;
                                lastName = lName;
                                username = uName;



//                                Get the pfp link from the database, if it is null, set it to the default pfp link
                                if (pfpLink != null){
                                    pfpStorageLink = pfpLink;
                                    pfpRef = storage.getReferenceFromUrl(pfpStorageLink);

                                    if (userID.equals(CurrentUser.getCurrent().getUserID())){ // saves doing it unneccesarily.
                                        initProfilePicBitmap();
                                    }

                                } else {
                                    Drawable vectorDrawable = VectorDrawableCompat.create(App.getContext().getResources(), R.drawable.baseline_person_24, null);
                                    pfpBitmap = App.drawableToBitmap(vectorDrawable);

                                }
                                callback.onUserLoaded(fName,lName, username, "pfpLink");
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Check if the username exists in the database
     * @param username
     * @param listener
     */
    public void checkUsernameExists(String username, DataLoadedListener listener) {

        CollectionReference usersCollection = db.collection("users");
        Query query = usersCollection.whereEqualTo("username", username);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        listener.OnDataLoaded(true);

                    } else {
                        listener.OnDataLoaded(false);
                    }
                } else {
                    Log.d(TAG, "err checking username");
                }
            }
        });
    }

    /***
     * Handle everything for getting pfp bitmap
     * Check if the profile picture exists locally, if not download it
     */
    public void initProfilePicBitmap(){
        if (localPfpFile.exists()) {
            // file already exists locally, no need to redownload
            this.pfpBitmap = BitmapFactory.decodeFile(localPfpFile.getAbsolutePath());

            if (pfpLoadedCallback != null) {
                pfpLoadedCallback.onPfpLoaded(pfpBitmap);
            }
        } else {
            dlProfilePicBitmap(App.getContext(), new User.PfpLoadedCallback() {
                @Override
                public void onPfpLoaded(Bitmap bitmap) {
                    pfpBitmap = bitmap;
                    // Call the callback if not null
                    if (pfpLoadedCallback != null) {
                        pfpLoadedCallback.onPfpLoaded(pfpBitmap);
                    }
                }
                @Override
                public void onPfpLoadFailed(Exception e) {
                    Log.d(TAG, "pfp load failed");
                }
            });
        }
    }

    /**
     * Used for Fetching Profile picture
     */
    public interface PfpLoadedCallback {
        void onPfpLoaded(Bitmap bitmap);
        void onPfpLoadFailed(Exception e);
    }

    public void setPfpLoadedCallback(PfpLoadedCallback callback) {
        this.pfpLoadedCallback = callback;
    }

    private PfpLoadedCallback pfpLoadedCallback;

    public File getLocalPfpFile(){
        return this.localPfpFile;
    }

    /***
     * Download the user's profile picture from the database if it doesn't currently exist in cache
     * @param context
     * @param callback
     */
    public void dlProfilePicBitmap(Context context, PfpLoadedCallback callback) { // made public for temp solution to the big async problem (ask noah)
//        File localFile = File.createTempFile("images", "jpg");
        Log.d(TAG, "downloading pfp from: "+pfpStorageLink);
        if (this.pfpStorageLink == null){
            return;
        }

        // use the local file if it exists
        if (localPfpFile.exists()){
            callback.onPfpLoaded(
                    BitmapFactory.decodeFile(localPfpFile.getAbsolutePath())
            );
            return;
        }

        pfpRef.getFile(localPfpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Download complete: "+ pfpStorageLink);
                // Local temp file has been created
                Bitmap pfpBitmap = BitmapFactory.decodeFile(localPfpFile.getAbsolutePath());
                callback.onPfpLoaded(pfpBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Download failed: "+ exception.toString() + " " + pfpStorageLink);
                callback.onPfpLoadFailed(new FileNotFoundException("File not found"));
            }
        });
    }




//    All getters and setters below
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUserID() {
        return userID;
    }
    public String getUsername() {
        return username;
    }
    public Bitmap getPfpBitmap() {
        return pfpBitmap;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Formats the name so it can be displayed on the screen
     * @param fName
     * @param lName
     * @return Formatted name of "Firstname Lastname"
     */
    public static String formatName(String fName, String lName){
        return String.format("%s %s", fName, lName); // e.g. "John Smith"
    }

    /***
     * Fetch the accounts that the user is following
     * @param listener
     */
    public void getFollowing(DataLoadedListener listener){
        db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> following = (List<String>) documentSnapshot.get("following");
                    listener.OnDataLoaded(following);
                }
        );
    }

    /***
     * Fetch the posts which are posted by this User
     * @param listener
     */
    public void getPosts(DataLoadedListener listener){
        Query postsQuery = db.collection("posts").whereEqualTo("author", this.userID);
        postsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> fetchedPosts = new ArrayList<Post>();
                Map<String, Object> currData;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    currData = document.getData();
                    fetchedPosts.add(new Post(
                            document.getId(),
                            currData.get("title"),
                            currData.get("body"),
                            currData.get("author"),
                            currData.get("publisher"),
                            currData.get("sourceURL"),
                            currData.get("timeStamp"),
                            new PostLoadCallback() {
                                @Override
                                public void onPostLoaded(Post post) {

                                }
                            }
                    ));
                }
                listener.OnDataLoaded(fetchedPosts);
            }
        });
    }

    public void addInitialisationCallback(InitialisationCallback initCallback) {
        if (isInitialised) {
            initCallback.onInitialised();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userID='" + userID + '\'' +
//                ", pfpRef=" + pfpRef.toString() +
//                ", pfpStorageLink='" + pfpStorageLink.toString() + '\'' + // we have second constructor so this wont work anymore if pfp isnt present (for the PostGenerator mainly)
                '}';
    }
}

