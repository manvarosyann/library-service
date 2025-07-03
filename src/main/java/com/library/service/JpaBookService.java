package com.library.service;

import com.library.model.BookEntity;
import com.library.repository.JpaBookRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class JpaBookService {
    private final JpaBookRepository bookRepository;

    public JpaBookService(JpaBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBook(BookEntity bookEntity) throws SQLException {
        bookRepository.save(bookEntity);
    }

    public void updateBook(BookEntity bookEntity) throws SQLException {
        bookRepository.save(bookEntity);
    }

    public void deleteBook(Long bookId) throws SQLException {
        bookRepository.deleteById(bookId);
    }

    public BookEntity getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

    public BookEntity getBookByTitle(String title) {
        return bookRepository.findByTitle(title).orElse(null);
    }

    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<BookEntity> getBooksByGenre(String genre) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
