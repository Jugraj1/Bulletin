package com.example.app_2100;

import com.example.app_2100.helper.DateFormatter;
import com.google.firebase.Timestamp;

import java.util.Date;


/**
 * Noah Vendrig
 */
public class Comment {
    private String authorID;    // ID of the author who made the comment
    private String text;        // Content of the comment
    private Timestamp timeStamp;    // Timestamp of when the comment was made
    private Date dateTime;      // Date and time representation of the timestamp


    /**
     * Constructs a Comment object with the given author ID, text, and timestamp
     * Initializes the date and time representation of the timestamp
     *
     * @param authorID (ID of the author who made the comment)
     * @param text (Content of the comment)
     * @param timeStamp (Timestamp of when the comment was made)
     */
    public Comment(String authorID, String text, Timestamp timeStamp) {
        this.authorID = authorID;
        this.text = text;
        this.timeStamp = timeStamp;
        // Initializing dateTime if timeStamp is not null
        if (timeStamp != null){
            this.dateTime = new Date(this.timeStamp.getSeconds() * 1000);
        }
    }

    /**
     * Gets the text content of the comment
     * @return The comment text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the formatted date and time string of when the comment was made
     * @return The formatted date and time string
     */
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

