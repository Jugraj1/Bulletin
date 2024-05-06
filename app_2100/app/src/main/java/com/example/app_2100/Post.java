package com.example.app_2100;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.Timestamp;
import java.util.Date;

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

    private String TAG = "Post";
    public Post(Object ID, Object title, Object body, Object authorID, Object publisher, Object sourceURL, Object timeStamp){
        this.ID = (String) ID;
        this.title = (String) title;
        this.body = (String) body;
        this.authorID = (String) authorID;
        this.publisher = (String) publisher;
        this.sourceURL = (String) sourceURL;
        this.timeStamp = (Timestamp) timeStamp;
        this.dateTime = new Date(this.timeStamp.getSeconds()*1000);

        if (this.authorID != null){
            FirestoreCallback userCallback = new FirestoreCallback(){
                @Override
                public void onUserLoaded(String fName, String lName){
                    authorName = User.formatName(fName, lName);
                }
            };

//            this.authorName = postAuthor.getFormattedName(); // set to the actual author
            User postAuthor = new User(this.authorID, userCallback);

            Log.d(TAG, postAuthor.toString());
        } else {
            this.authorName = "INVALID";
        }
    }


    @Override
    public String toString(){
        return "Post {" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", authorID='" + authorID + '\'' +
                ", authorName='" + authorName + '\'' +
                ", publisher='" + publisher + '\'' +
                ", sourceURL='" + sourceURL + '\'' +
                ", timeStamp=" + timeStamp +
                ", dateTime=" + dateTime +
                '}';
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
