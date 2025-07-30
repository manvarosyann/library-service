package com.library.controller;

import com.library.model.BookDto;
import com.library.service.JpaBookServiceDto;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<BookDto>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) List<Long> authorIds,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false) Integer minPages,
            @RequestParam(required = false) Integer maxPages
    ) {
        List<BookDto> books = bookServiceDto.getFilteredBooks(title, sectionId,
                authorIds, sortBy, direction, minPages, maxPages);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto bookDto = bookServiceDto.getBookById(id);
        return bookDto != null ? ResponseEntity.ok(bookDto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookDto bookDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookServiceDto.createBook(bookDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody @Valid BookDto bookDto) {
        return ResponseEntity.ok(bookServiceDto.updateBook(id, bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookServiceDto.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
