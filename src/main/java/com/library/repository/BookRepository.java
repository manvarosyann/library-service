package com.library.repository;

import com.library.model.Book;

import java.util.List;

public interface BookRepository {
    void save(Book book);

    void update(Book book);

    void delete(String bookID);

    Book findByID(String bookID);

    Book findByTitle(String title);

    List<Book> findAll();

    List<Book> findByGenre(String genre);
}
