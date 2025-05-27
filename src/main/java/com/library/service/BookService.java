package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;

import java.sql.SQLException;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBook(Book book) throws SQLException {
        bookRepository.save(book);
    }

    public void updateBook(Book book) throws SQLException {
        bookRepository.update(book);
    }

    public void deleteBook(String bookId) throws SQLException {
        bookRepository.delete(bookId);
    }

    public Book getBookById(String bookId) {
        return bookRepository.findByID(bookId);
    }

    public Book getBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }
}
