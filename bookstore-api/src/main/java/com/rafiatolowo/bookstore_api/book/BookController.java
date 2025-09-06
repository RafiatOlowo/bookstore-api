package com.rafiatolowo.bookstore_api.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rafiatolowo.bookstore_api.book.exceptions.BookNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * The REST controller for managing books.
 * <p>
 * This class handles all incoming HTTP requests related to the book resource.
 * It serves as the entry point for the RESTful API, mapping URLs to methods
 * that perform CRUD (Create, Read, Update, Delete) operations on books.
 * All methods here are designed to work with the custom global exception handler
 * to provide meaningful error responses.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

     /**
     * Constructs a new BookController with the specified BookService.
     *
     * @param bookService The service component to handle business logic for books.
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Retrieves a list of all books.
     * <p>
     * This method handles GET requests to "/api/book". It fetches a list of
     * all available books in the system.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link Book} objects
     * and an HTTP status of OK.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * Saves a new book.
     * <p>
     * This method handles POST requests to "/api/book".  It creates a new book
     * resource using the data provided in the request body.
     *
     * @param book The book object to be saved, passed in the request body.
     * @return A {@link ResponseEntity} containing the newly saved {@link Book} object
     * and an HTTP status of CREATED.
     */
    @PostMapping
    public ResponseEntity<Object> addBook(@RequestBody Book book) {
        Book createdBook = bookService.addBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

     /**
     * Retrieves a single book by its ISBN.
     * <p>
     * This method handles GET requests to "/api/book/{isbn}". It retrieves
     * a specific book based on the ISBN provided as a path variable.
     *
     * @param isbn The ISBN of the book to retrieve.
     * @return A {@link ResponseEntity} containing the found {@link Book} object
     * and an HTTP status of OK.
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found with ISBN: " + isbn);
        }
    }

    /**
     * Retrieves book(s) written by a specified author.
     * <p>
     * This method handles GET requests to "/api/book/author/{author}".
     * It retrieves a list of books written by the specified author.
     *
     * @param author The name of the author to search for.
     * @return A {@link ResponseEntity} containing a list of books by the specified author and an HTTP status of OK.
     * The list may be empty if no books are found by the author.
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        List<Book> books = bookService.findBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    /**
     * Partially updates an existing book by its ISBN.
     * <p>
     * This method handles PATCH requests to "/api/book/{isbn}". It modifies one or more
     * fields of an existing book based on the request body.
     *
     * @param isbn The ISBN of the book to update.
     * @param updatedBook The updated book object containing the fields to be modified.
     * @return The updated book with an HTTP status of OK.
     * If the book is not found, the {@link com.rafiatolowo.bookstore_api.book.BookNotFoundException}
     * is thrown and handled by the global exception handler, returning a 404 Not Found status.
     */
    @PatchMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book updatedBook) {
        Book book = bookService.updateBook(isbn, updatedBook);
        return ResponseEntity.ok(book);
    }

    /**
     * Deletes a book from the inventory by its ISBN.
     * <p>
     * This method handles DELETE requests to "/api/book/{isbn}". It removes the book
     * that matches the ISBN provided as a path variable.
     *
     * @param isbn The ISBN of the book to delete.
     * @return A {@link ResponseEntity} with an HTTP status of NO_CONTENT, indicating
     * successful deletion.
     * If the book is not found, the {@link com.rafiatolowo.bookstore_api.book.BookNotFoundException}
     * is thrown and handled by the global exception handler, returning a 404 Not Found status.
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBookByIsbn(@PathVariable String isbn) {
        bookService.deleteBookByIsbn(isbn);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exception handler for when a book is not found.
     * This method catches the BookNotFoundException and returns an HTTP 404 Not Found status.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException ex) {
        return ex.getMessage();
    }
}
