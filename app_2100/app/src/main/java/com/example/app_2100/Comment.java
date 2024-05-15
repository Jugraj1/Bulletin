package com.example.app_2100;

import com.example.app_2100.helper.DateFormatter;
import com.google.firebase.Timestamp;

import java.util.Date;

public class Comment {
    private String authorID;
    private String text;
    private Timestamp timeStamp;
    private Date dateTime;
    String authorName;

    public Comment(String authorID, String text, Timestamp timeStamp) {
        this.authorID = authorID;
        this.text = text;
        this.timeStamp = timeStamp;
        if (timeStamp!= null){
            this.dateTime = new Date(this.timeStamp.getSeconds()*1000);
        }

        // onUserLoaded callback isnt being triggered for some weird reason, leave comment author out
//        User commentAuthor = new User((String) authorID, new FirestoreCallback(){
//            @Override
//            public void onUserLoaded(String fName, String lName, String pfpLink){
//                Log.d("PostView", "done");
//                authorName = User.formatName(fName, lName);
//
////                        Log.d(TAG, "authorName: "+authorName);
//                listener.OnDataLoaded(authorName);
//            }
//        });

    }



    public String getAuthorID() {
        return authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getText() {
        return text;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getFormattedDateTime() {
        return DateFormatter.formatDate(dateTime);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "authorID='" + authorID + '\'' +
                ", text='" + text + '\'' +
                ", timeStamp=" + timeStamp +
                ", dateTime=" + dateTime +
                '}';
    }
}
