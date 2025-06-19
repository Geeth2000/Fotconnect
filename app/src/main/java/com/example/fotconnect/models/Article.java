package com.example.fotconnect.models;

import com.google.firebase.database.Exclude; // Import Exclude for Firebase serialization control

// This class represents a single article item in the application.
// It holds data such as title, content, image URL, and a timestamp.
public class Article {
    private String title;
    private String content; // Content of the article, previously named summary
    private String imageUrl; // URL for the article's image, replacing local resource ID
    private long timestamp; // Timestamp for sorting articles (e.g., by publication date)

    // Default constructor required for Firebase Realtime Database to deserialize data.
    public Article() {
        // Public no-argument constructor
    }

    // Constructor to initialize an Article object with all its properties.
    public Article(String title, String content, String imageUrl, long timestamp) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getter method for the article title.
    public String getTitle() {
        return title;
    }

    // Setter method for the article title. Used by Firebase for object mapping.
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter method for the article content.
    public String getContent() {
        return content;
    }

    // Setter method for the article content. Used by Firebase for object mapping.
    public void setContent(String content) {
        this.content = content;
    }

    // Getter method for the article image URL.
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter method for the article image URL. Used by Firebase for object mapping.
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter method for the article timestamp.
    public long getTimestamp() {
        return timestamp;
    }

    // Setter method for the article timestamp. Used by Firebase for object mapping.
    // This setter is crucial for handling cases where Firebase might store timestamp as a String.
    public void setTimestamp(Object timestamp) {
        if (timestamp instanceof Long) {
            this.timestamp = (Long) timestamp;
        } else if (timestamp instanceof String) {
            try {
                this.timestamp = Long.parseLong((String) timestamp);
            } catch (NumberFormatException e) {
                // Log the error or handle it appropriately if parsing fails
                // For now, setting to 0 or current time for robustness
                this.timestamp = 0; // Or System.currentTimeMillis();
                System.err.println("Error parsing timestamp string: " + timestamp + " - " + e.getMessage());
            }
        } else {
            this.timestamp = 0; // Default or error value
        }
    }

    // This @Exclude annotation prevents Firebase from trying to serialize this method,
    // as it's only for internal parsing logic.
    @Exclude
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}