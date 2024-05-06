package com.example.app_2100;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String userID;
    private String pfpStorageLink;

    private String pfpLocalLink;
    private String TAG = "User";
    private FirebaseFirestore db = FirebaseFirestoreConnection.getDb().getInstance();
    private Boolean isInitialised = false;
    public InitialisationCallback initCallback;

    private Bitmap pfpBitmap;

    private File localPfpFile;
    FirebaseStorage storage;
    StorageReference pfpRef;

    public User(String userID, FirestoreCallback callback){
        this.userID = userID;
        this.pfpLocalLink = String.format("pfp_%s.jpg", this.userID);
        this.pfpStorageLink = String.format("gs://app-f4755.appspot.com/pfp/%s.jpg", this.userID);
        storage = FirebaseStorage.getInstance();
//        this.pfpStorageLink = "gs://app-f4755.appspot.com/pfp/" + this.userID + ".jpg";
        queryUserByID(this.userID, callback);
        // synchronised
         // file structure is root/pfp/{userId}.jpg
    }

//    public FirestoreCallback getUserCallback() {
//        return userCallback;
//    }

    public void addInitialisationCallback(InitialisationCallback initCallback) {
        if (isInitialised) {
            initCallback.onInitialised();
        } else {
            // !?
        }
    }

    public void queryUserByID(String userID, FirestoreCallback callback){
//        CollectionReference usersRef = db.collection("users");
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
                                String pfpLink = (String) userData.get("pfpStorageLink"); // may be null

                                firstName = fName;
                                lastName = lName;
                                if (pfpLink != null){
                                    pfpStorageLink = pfpLink;
                                    pfpRef = storage.getReferenceFromUrl(pfpStorageLink);

                                    if (userID.equals(CurrentUser.getCurrent().getUserID())){ // saves doing it unneccesarily.
                                        Log.d(TAG, "link: "+pfpStorageLink);
                                        initProfilePicBitmap();
                                    }

                                    Log.d(TAG, "pfpLink: "+ pfpStorageLink);
//                                    Log.d(TAG, "uid: "+userID);
//                                    Log.d(TAG, "curr:" +CurrentUser.getCurrent().getUserID());
                                }
//                                isInitialised = true;
//                                initCallback.onUserInitialised();
                                callback.onUserLoaded(fName, lName, pfpLink);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /***
     * handle everything for getting pfp bitmap
     */
    public void initProfilePicBitmap(){
        this.localPfpFile = new File(App.getContext().getCacheDir(), "pfp_"+this.userID+".jpg");
        if (localPfpFile.exists()) {
            // file already exists locally, no need to redownload
            Log.d(TAG, "File already exists: " + localPfpFile.getAbsolutePath());
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

    public interface PfpLoadedCallback {
        void onPfpLoaded(Bitmap bitmap);
        void onPfpLoadFailed(Exception e);
    }
    public void setPfpLoadedCallback(PfpLoadedCallback callback) {
        this.pfpLoadedCallback = callback;
    }

    private PfpLoadedCallback pfpLoadedCallback;

    public void dlProfilePicBitmap(Context context, PfpLoadedCallback callback) { // made public for temp solution to the big async problem (ask noah)
//        File localFile = File.createTempFile("images", "jpg");
        Log.d(TAG, "pfp link get: "+pfpStorageLink);
        if (this.pfpStorageLink == null){
            return;
        }

        File localFile = new File(context.getCacheDir(), this.pfpLocalLink);
        pfpRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Download complete: "+ pfpStorageLink);
                // Local temp file has been created
                Bitmap pfpBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                callback.onPfpLoaded(pfpBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Download failed: "+ pfpStorageLink);
                callback.onPfpLoadFailed(new FileNotFoundException("File not found"));
            }
        });
    }

    public void updateProfilePicture(Bitmap imgBitmap){
//        task = storageRef.put(file);
//
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = pfpRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public User(){

    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID() {
        return userID;
    }

    public Bitmap getPfpBitmap() {
        return pfpBitmap;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPfpStorageLink(String pfpStorageLink) {
        this.pfpStorageLink = pfpStorageLink;
    }

    public static String formatName(String fName, String lName){
        return String.format("%s %s", fName, lName); // e.g. "John Smith"
    }

    public void setPfpBitmap(Bitmap bmp){

    }


    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userID='" + userID + '\'' +
                ", pfpRef=" + pfpRef.toString() +
                ", pfpStorageLink='" + pfpStorageLink.toString() + '\'' +
                '}';
    }
}

