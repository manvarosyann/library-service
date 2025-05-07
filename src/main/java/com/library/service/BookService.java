package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;

import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        bookRepository.update(book);
    }

    public void deleteBook(String bookId) {
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
