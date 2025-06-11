package com.library.app;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LibraryStartupRunner {
    private final BookService bookService;

    public LibraryStartupRunner(BookService bookService) {
        this.bookService = bookService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            Book book = new Book("1", "Digital Fortress", "Dan Brown", "Thriller");
            bookService.createBook(book);
            System.out.println("Book created");

            Book found = bookService.getBookByTitle("Digital Fortress");
            if (found != null) {
                System.out.println("Book found: " + found);
            } else {
                System.out.println("Book not found");
            }

            List<Book> allBooks = bookService.getAllBooks();
            System.out.println("\nAll books found: ");
            allBooks.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
