package com.library.repository;

import com.library.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> booksByID = new HashMap<>();
    private final Map<String, Book> booksByTitle = new HashMap<>();

    @Override
    public void save(Book book) {
        booksByID.put(book.getBookID(), book);
        booksByTitle.put(book.getTitle(), book);
    }

    @Override
    public void update(Book book) {
        booksByID.put(book.getBookID(), book);
        booksByTitle.put(book.getTitle(), book);
    }

    @Override
    public void delete(String bookID) {
        Book book = booksByID.remove(bookID);
        if (book != null) {
            booksByTitle.remove(book.getTitle());
        }
    }

    @Override
    public Book findByID(String bookID) {
        return booksByID.get(bookID);
    }

    @Override
    public Book findByTitle(String title) {
        return booksByTitle.get(title);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(booksByID.values());
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return booksByID.values().stream().
                filter(book -> book.getGenre().
                        equals(genre)).toList();
    }
}
