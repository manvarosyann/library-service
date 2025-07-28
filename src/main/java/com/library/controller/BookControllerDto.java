package com.library.controller;

import com.library.model.BookDto;
import com.library.service.JpaBookServiceDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookControllerDto {

    private final JpaBookServiceDto bookServiceDto;

    public BookControllerDto(JpaBookServiceDto bookServiceDto) {
        this.bookServiceDto = bookServiceDto;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookServiceDto.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto bookDto = bookServiceDto.getBookById(id);
        return bookDto != null ? ResponseEntity.ok(bookDto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookServiceDto.createBook(bookDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookServiceDto.updateBook(id, bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookServiceDto.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
