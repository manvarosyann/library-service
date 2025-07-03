package com.library.service;

import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")

public class SpringBookServiceTest {
    @Autowired
    private BookService bookService;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void clearDatabaseAndInsertSection() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("truncate table book restart identity cascade");
            stmt.execute("truncate table section restart identity cascade");
            stmt.execute("insert into section (section_id, name) values (1, 'Default Section')");
        }
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
}
