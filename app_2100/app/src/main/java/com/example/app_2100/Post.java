package com.example.app_2100;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Parcelable {

    private String ID;
    private String title;
    private String body;
    private String authorID;
    private String authorName;
    private String publisher;
    private String sourceURL;
    private Timestamp timeStamp;
    private Date dateTime;

    private double score;
    private List<String> likes;

    private Boolean isLikedByCurrUser;
    private Boolean isSharedByCurrUser;
    private DocumentReference ref;
    FirebaseFirestore db;
    private String TAG = "Post";
    private PostLoadCallback postLoadCallback;

    /***
     * For CreatePost
     * @param title
     * @param body
     * @param authorID
     * @param publisher
     * @param sourceURL
     * @param timeStamp
     */
    public Post(String title, String body, String authorID, String publisher, String sourceURL, Timestamp timeStamp){
        this.title = title;
        this.body = body;
        this.authorID = authorID;
        this.publisher = publisher;
        this.sourceURL = sourceURL;
        this.timeStamp = timeStamp;
    }

    public Post(Object ID, Object title, Object body, Object authorID, Object publisher, Object sourceURL, Object timeStamp, PostLoadCallback callback){
        this.ID = (String) ID;
        this.title = (String) title;
        this.body = (String) body;
        this.authorID = (String) authorID;
        User postAuthor = new User((String) authorID, new FirestoreCallback(){
            @Override
            public void onUserLoaded(String fName, String lName, String pfpLink){
                authorName = User.formatName(fName, lName);

//                        Log.d(TAG, "authorName: "+authorName);
                callback.onPostLoaded(Post.this);
            }
        });

        this.publisher = (String) publisher;
        this.sourceURL = (String) sourceURL;
        this.timeStamp = (Timestamp) timeStamp;
        this.dateTime = new Date(this.timeStamp.getSeconds()*1000);
        this.score = 0;
        this.likes = new ArrayList<>();
        db = FirebaseFirestoreConnection.getDb();
        ref = db.collection("posts").document(this.ID);
        isLikedByCurrUser = false;
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        score = (double) document.get("score");

                        if (likes != null){
                            likes = (List<String>) document.get("likes");
                            isLikedByCurrUser = likes.contains(CurrentUser.getCurrent().getUserID());
                        }
//
                    } else {
                        Log.d(TAG, "Document doesnt exist :(");
                    }
                } else {
                    Log.d(TAG, "failed to get document: ", task.getException());
                }
            }
        });
    }

    public void toggleLike(String likerID){
//        Log.d(TAG, String.valueOf(isLikedByCurrUser));

        // update score

        if (isLikedByCurrUser){
            ref.update("likes", FieldValue.arrayRemove(likerID))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, String.format("Error updating Post with removing liker: %s", likerID), e);
                    }
                });
        } else {
            ref.update("likes", FieldValue.arrayUnion(likerID)) // append to the array of likers
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, String.format("Error updating Post with new liker: %s", likerID), e);
                    }
                });
        }

        isLikedByCurrUser = !isLikedByCurrUser;
    }

    public Boolean getLikedByCurrUser() {
        return isLikedByCurrUser;
    }
    public void setIsLikedByCurrUser(boolean b) {
        this.isLikedByCurrUser = b;
    }


    public Boolean getSharedByCurrUser() {
        return isSharedByCurrUser;
    }

    public void addShare(String likerID){
        DocumentReference currPostRef = FirebaseFirestoreConnection.getDb()
                .collection("posts").document(this.ID);
        currPostRef
                .update("shares", FieldValue.arrayUnion(likerID)) // append to the array of likers
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, String.format("Error updating Post with new liker: %s", likerID), e);
                    }
                });
    }

    public void removeShare(String likerID){
        DocumentReference currPostRef = FirebaseFirestoreConnection.getDb()
                .collection("posts").document(this.ID);
        currPostRef
                .update("shares", FieldValue.arrayUnion(likerID)) // append to the array of likers
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, String.format("Error updating Post with new liker: %s", likerID), e);
                    }
                });
    }


    @Override
    public String toString(){
        return "Post {" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", authorID='" + authorID + '\'' +
                ", authorName='" + authorName + '\'' +
                ", isLikedByCurrUser=" + isLikedByCurrUser  +
//                ", likes=" + likes.toString() +
//                ", score=" + String.valueOf(score) +
                ", publisher='" + publisher + '\'' +
                ", sourceURL='" + sourceURL + '\'' +
                ", timeStamp=" + timeStamp +
                ", dateTime=" + dateTime +
                '}';
    }

    public void setPostLoadCallback(PostLoadCallback callback) {
        postLoadCallback = callback;
    }

    // Invoke the callback method when the post is loaded
    private void invokePostLoadCallback() {
        if (postLoadCallback != null) {
            postLoadCallback.onPostLoaded(this);
        }
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public String getDateTime() {
        return dateTime.toString();
    }

    public String getFormattedDateTime() {
        return DateFormatter.formatDate(dateTime);
    }

    // Stuff for parceling into intent:
    protected Post(Parcel in) {
        ID = in.readString();
        title = in.readString();
        body = in.readString();
        authorID = in.readString();
        authorName = in.readString();
        publisher = in.readString();
        sourceURL = in.readString();
        timeStamp = in.readParcelable(Timestamp.class.getClassLoader());
        dateTime = new Date(timeStamp.getSeconds()*1000);

//        isLikedByCurrUser = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(authorID);
        dest.writeString(authorName);
        dest.writeString(publisher);
        dest.writeString(sourceURL);
//        dest.writeByte((byte) (isLikedByCurrUser ? 1 : 0)); // needed for writing bools
        dest.writeParcelable(timeStamp, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

}
