package com.library.controller;

import com.library.dto.BookFilterRequest;
import com.library.model.BookCreateDto;
import com.library.model.BookResponseDto;
import com.library.service.JpaBookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final JpaBookService bookService;

    public BookController(JpaBookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Librarian', 'Member')")
    public ResponseEntity<Page<BookResponseDto>> getAllBooks(
            @ModelAttribute BookFilterRequest filter,
            Pageable pageable
    ) {
        Page<BookResponseDto> books = bookService.getFilteredBooks(filter, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Librarian','Member')")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        BookResponseDto dto = bookService.getBookById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Librarian')")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody @Valid BookCreateDto bookCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookCreateDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@bookSecurity.canModifyBook(#id, authentication)")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id, @RequestBody @Valid BookCreateDto bookCreateDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookCreateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@bookSecurity.canModifyBook(#id, authentication)")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
