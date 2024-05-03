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
    private String TAG = "User";
    private FirebaseFirestore db = FirebaseFirestoreConnection.getDb().getInstance();

    FirebaseStorage storage;
    StorageReference pfpRef;
    String pfpStorageLink;

    public User(String userID, FirestoreCallback callback){
        this.userID = userID;
        storage = FirebaseStorage.getInstance();
        this.pfpStorageLink = "gs://app-f4755.appspot.com/pfp/" + this.userID + ".jpg";
        this.pfpRef = storage.getReferenceFromUrl(pfpStorageLink); // file structure is root/pfp/{userId}.jpg
        queryUserByID(this.userID, callback);
    }

//    public FirestoreCallback getUserCallback() {
//        return userCallback;
//    }

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
                                String pfp = (String) userData.get("profilePicLink"); // may be null
                                Log.d(TAG, User.formatName(fName, lName));
                                callback.onUserLoaded(fName, lName);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static String formatName(String fName, String lName){
        return String.format("%s %s", fName, lName); // e.g. "John Smith"
    }

    public interface PfpLoadedCallback {
        void onPfpLoaded(Bitmap bitmap);
        void onPfpLoadFailed(Exception e);
    }

    public void getProfilePicBitmap(Context context, PfpLoadedCallback callback) {
//        File localFile = File.createTempFile("images", "jpg");
        File localFile = new File(context.getCacheDir(), String.format("pfp_%s.jpg", this.userID));
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

