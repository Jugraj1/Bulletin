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
