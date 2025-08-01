package com.library.model;


import lombok.Data;

@Data
public class Book {
    private final String bookID;
    private String title;
    private String author;
    private String genre;
    private boolean isAvailable;

    public Book(String bookID, String title, String author, String genre) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isAvailable = true;
    }
}
