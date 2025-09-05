package com.rafiatolowo.bookstore_api.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * The REST controller for the Bookstore API.
 * This class handles all incoming web requests related to books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * Constructor for dependency injection.
     */
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint to retrieve a list of all books.
     * GET /api/books
     * @return A ResponseEntity containing a list of all books and an OK status.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * Endpoint to add a new book to the database.
     * POST /api/books
     * The try-catch block is used to handle a specific business exception.
     * @param book The book object received from the request body.
     * @return A ResponseEntity with the created book and a CREATED status, or a CONFLICT status with an error message.
     */
    @PostMapping
    public ResponseEntity<Object> addBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.addBook(book);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Return the exception message in the response body.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

/**
     * GET endpoint to retrieve a single book by its ISBN.
     * The Optional is handled with an if-else block to provide a specific
     * error response when the book is not found.
     * @param isbn The unique ISBN of the book.
     * @return The book with the specified ISBN, or a 404 Not Found error with a message.
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
     * GET endpoint to find books by author.
     * @param author The name of the author to search for.
     * @return A list of books by the specified author.
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        List<Book> books = bookService.findBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    /**
     * PATCH endpoint to partially update an existing book by its ISBN.
     * This method modifies one or more fields of an existing book.
     * A try-catch block handles the case where the book to update is not found.
     * @param isbn The ISBN of the book to update.
     * @param updatedBook The updated book object containing the fields to be modified.
     * @return The updated book, or a 404 Not Found error with a message.
     */
    @PatchMapping("/{isbn}")
    public ResponseEntity<Object> updateBook(@PathVariable String isbn, @RequestBody Book updatedBook) {
        try {
            Book book = bookService.updateBook(isbn, updatedBook);
            return ResponseEntity.ok(book);
        } catch (IllegalStateException e) {
            // Return the exception message in the response body.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * DELETE endpoint to delete a book from the inventory by its ISBN.
     * The boolean return value from the service is checked to determine the response.
     * @param isbn The ISBN of the book to delete.
     * @return A 204 No Content status on success, or a 404 Not Found error with a message.
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBookByIsbn(@PathVariable String isbn) {
        if (bookService.deleteBookByIsbn(isbn)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found with ISBN: " + isbn);
        }
    }
}
