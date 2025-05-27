package com.library.repository;

import com.library.model.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookRepository {
    void save(Book book) throws SQLException;

    void update(Book book) throws SQLException;

    void delete(String bookID) throws SQLException;

    Book findByID(String bookID);

    Book findByTitle(String title);

    List<Book> findAll();

    List<Book> findByGenre(String genre);
}
