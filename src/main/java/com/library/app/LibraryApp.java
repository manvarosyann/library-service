package com.library.app;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.InMemoryBookRepository;
import com.library.repository.JdbcBookRepository;
import com.library.service.BookService;

import java.sql.SQLException;
import java.util.List;

public class LibraryApp {
    public static void main(String[] args) throws SQLException {
        // Using InMemoryBookRepository
//        BookService bookService = new BookService(new InMemoryBookRepository());
//
//        Book book1 = new Book("1", "Clean Code", "Robert Martin", "Programming");
//        Book book2 = new Book("2", "The Hobbit", "J.R.R. Tolkien", "Fantasy");
//        Book book3 = new Book("3", "Foundation", "Isaac Asimov", "Science Fiction");
//        Book book4 = new Book("4", "1984", "George Orwell", "Dystopia");
//
//        //Create
//        bookService.createBook(book1);
//        bookService.createBook(book2);
//        bookService.createBook(book3);
//        bookService.createBook(book4);
//
//        //Read
//        System.out.println("All books: \n");
//        bookService.getAllBooks().forEach(System.out::println);
//
//        //Search by title
//        System.out.println("Search by title 'Foundation'");
//        System.out.println(bookService.getBookByTitle("Foundation"));
//
//        //Search by genre
//        System.out.println("Books in genre 'Fantasy'");
//        bookService.getBooksByGenre("Fantasy").forEach(System.out::println);
//
//        //Renting a book
//        System.out.println("Renting '1984'");
//        Book bookToRent = bookService.getBookByTitle("1984");
//        bookToRent.setAvailable(false);
//        bookService.updateBook(bookToRent);
//
//        //View currently rented books
//        System.out.println("Currently rented books: ");
//        List<Book> rentedBooks = bookService.getAllBooks().stream().filter(book -> !book.isAvailable()).toList();
//        rentedBooks.forEach(System.out::println);
//
//        //Returning a book
//        System.out.println("Returning '1984'");
//        bookToRent.setAvailable(true);
//        bookService.updateBook(bookToRent);
//
//        //Update
//        book2.setGenre("Drama");
//        book2.setAvailable(false);
//        bookService.updateBook(book2);
//        System.out.println("Updated Book 2: " + bookService.getBookById("2"));
//
//        //Delete
//        bookService.deleteBook("1");
//        System.out.println("After deletion: ");
//        bookService.getAllBooks().forEach(System.out::println);
//
//        //Final state of Library
//        System.out.println("Final state of Library: ");
//        bookService.getAllBooks().forEach(System.out::println);


        // Using JdbcBookRepository
        BookRepository repository = new JdbcBookRepository();
        BookService bookService = new BookService(repository);

        try {
            // Create a book
            Book book = new Book("1", "Digital Fortress", "Dan Brown", "Thriller");
            bookService.createBook(book);
            System.out.println("Book created");

            // Fetch by title
            Book found = bookService.getBookByTitle("Digital Fortress");
            if (found != null) {
                System.out.println("Book found" + found);
            } else {
                System.out.println("Book not found");
            }

            // List all books
            List<Book> allBooks = bookService.getAllBooks();
            System.out.println("All books found: ");
            allBooks.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
