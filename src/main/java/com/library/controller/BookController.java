package com.library.controller;

import com.library.model.BookEntity;
import com.library.service.JpaBookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    private final JpaBookService bookService;

    public BookController(JpaBookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<BookEntity> getAllBooks() {
        return bookService.getAllBooks();
    }
}
