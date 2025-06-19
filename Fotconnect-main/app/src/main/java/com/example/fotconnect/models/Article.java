package com.example.fotconnect.models;

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
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
