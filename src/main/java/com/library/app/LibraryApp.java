package com.library.app;

import com.library.model.Book;
import com.library.repository.InMemoryBookRepository;
import com.library.service.BookService;

public class LibraryApp {
    public static void main(String[] args) {
        BookService bookService = new BookService(new InMemoryBookRepository());

        Book book1 = new Book("1", "Clean Code", "Robert Martin", "Programming");
        Book book2 = new Book("2", "The Hobbit", "John Smith", "Fantasy");

        //Create
        bookService.createBook(book1);
        bookService.createBook(book2);

        //Read
        System.out.printf("All books: \n");
        bookService.getAllBooks().forEach(System.out::println);

        //Update
        book2.setGenre("Drama");
        book2.setAvailable(false);
        bookService.updateBook(book2);
        System.out.println("Updated Book 2: " + bookService.getBookById("2"));

        //Delete
        bookService.deleteBook("1");
        System.out.println("After deletion: ");
        bookService.getAllBooks().forEach(System.out::println);
    }
}
