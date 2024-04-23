package com.example.app_2100;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Post {

    private String title;
    private String body;
    private String authorID;
    private String authorName;
    private String authorName;
    private String publisher;
    private String sourceURL;
    private Timestamp timeStamp;
    private Date dateTime;

    public Post(Object title, Object body, Object authorID, Object publisher, Object sourceURL, Object timeStamp){
        this.title = (String) title;
        this.body = (String) body;
        this.authorID = (String) authorID;
        this.publisher = (String) publisher;
        this.sourceURL = (String) sourceURL;
        this.timeStamp = (Timestamp) timeStamp;
        this.dateTime = new Date(this.timeStamp.getSeconds()*1000);
        this.authorName = "author";
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
}
