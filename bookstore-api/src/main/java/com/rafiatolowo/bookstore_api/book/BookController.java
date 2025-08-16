package com.rafiatolowo.bookstore_api.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for managing the book inventory.
 * Defines API endpoints for CRUD operations.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * GET endpoint to retrieve a list of all books in the inventory.
     * @return A list of all books.
     */
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * POST endpoint to create a new book entry.
     * @param book The book object to be created.
     * @return The created book with a 201 Created status.
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    /**
     * GET endpoint to retrieve a single book by its ISBN.
     * @param isbn The unique ISBN of the book.
     * @return The book with the specified ISBN, or a 404 Not Found error.
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET endpoint to find books by author.
     * @param author The name of the author to search for.
     * @return A list of books by the specified author.
     */
    @GetMapping("/author/{author}")
    public List<Book> getBooksByAuthor(@PathVariable String author) {
        return bookRepository.findByAuthor(author);
    }

    /**
     * PUT endpoint to update an existing book by its ISBN.
     * @param isbn The ISBN of the book to update.
     * @param updatedBook The updated book object.
     * @return The updated book, or a 404 Not Found error.
     */
    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book updatedBook) {
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook != null) {
            // Update the existing book's fields.
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setStock(updatedBook.getStock());
            
            // This is a simplified update. In a real-world app, you'd handle
            // specific subclass fields as well.
            
            bookRepository.save(existingBook);
            return ResponseEntity.ok(existingBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE endpoint to delete a book from the inventory by its ISBN.
     * @param isbn The ISBN of the book to delete.
     * @return A 204 No Content status on success, or a 404 Not Found error.
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBookByIsbn(@PathVariable String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            bookRepository.delete(book);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
