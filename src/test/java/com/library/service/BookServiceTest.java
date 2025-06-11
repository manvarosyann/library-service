package com.library.service;

import com.library.model.Book;
import com.library.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookServiceTest {
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(new InMemoryBookRepository());
    }

    @Test
    void testCreateAndFindBookById() throws SQLException {
        Book book = new Book("1", "Clean Code", "Robert Martin", "Programming");
        bookService.createBook(book);

        Book result = bookService.getBookById("1");
        assertEquals("Clean Code", result.getTitle());
        assertTrue(result.isAvailable());
    }

    @Test
    void testUpdateBook() throws SQLException {
        Book book = new Book("1", "Clean Code", "Robert Martin", "Programming");
        bookService.createBook(book);

        book.setAvailable(false);
        book.setTitle("New Title");
        bookService.updateBook(book);

        Book updatedBook = bookService.getBookById("1");
        assertEquals("New Title", updatedBook.getTitle());
        assertFalse(updatedBook.isAvailable());
    }

    @Test
    void testDeleteBook() throws SQLException {
        Book book = new Book("3", "1984", "George Orwell", "Dystopia");
        bookService.createBook(book);
        bookService.deleteBook("3");

        assertNull(bookService.getBookById("3"));
    }

    @Test
    void testFindAllBooks() throws SQLException {
        bookService.createBook(new Book("1", "Clean Code", "Robert Martin", "Programming"));
        bookService.createBook(new Book("2", "1984", "George Orwell", "Dystopia"));

        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(2, allBooks.size());
    }

    @Test
    void testFindBookByTitle() throws SQLException {
        Book book = new Book("1", "Clean Code", "Robert Martin", "Programming");
        bookService.createBook(book);

        Book found = bookService.getBookByTitle("Clean Code");
        assertNotNull(found);
        assertEquals("Clean Code", found.getTitle());
    }

    @Test
    void testFindBookByGenre() throws SQLException {
        bookService.createBook(new Book("1", "Book X", "Author X", "Horror"));
        bookService.createBook(new Book("2", "Book Y", "Author Y", "Horror"));
        bookService.createBook(new Book("3", "Book Z", "Author Z", "Fantasy"));

        List<Book> horrorBooks = bookService.getBooksByGenre("Horror");
        assertEquals(2, horrorBooks.size());

        List<Book> fantasyBooks = bookService.getBooksByGenre("Fantasy");
        assertEquals(1, fantasyBooks.size());
    }
}
