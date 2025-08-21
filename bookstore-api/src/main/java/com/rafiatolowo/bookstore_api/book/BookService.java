package com.rafiatolowo.bookstore_api.book;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * The service class for handling all business logic related to Book entities.
 * This class acts as a middle layer between the BookController and the BookRepository.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Constructor for dependency injection. Spring will automatically
     * inject the BookRepository instance.
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieves all books from the database.
     * @return A list of all Book entities.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Finds a book by its unique ISBN.
     * This method now returns a single Optional, regardless of whether the
     * repository method also returns an Optional.
     * @param isbn The ISBN of the book to find.
     * @return An Optional containing the found book, or an empty Optional if not found.
     */
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    /**
     * Adds a new book to the database, including business logic.
     * For example, it checks if a book with the same ISBN already exists
     * before saving a new one.
     * @param book The book object to save.
     * @return The saved book entity.
     * @throws IllegalStateException if a book with the same ISBN already exists.
     */
    public Book addBook(Book book) {
        // Business logic: Check for duplicates before saving.
        Optional<Book> existingBook = findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            throw new IllegalStateException("A book with ISBN " + book.getIsbn() + " already exists.");
        }
        return bookRepository.save(book);
    }
    
    /**
     * Finds a list of books by a specific author.
     * @param author The name of the author to search for.
     * @return A list of books by the specified author.
     */
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

     /**
     * Updates an existing book. This method handles partial updates by
     * only updating the fields that are not null in the request body.
     * @param isbn The ISBN of the book to update.
     * @param updatedBook The book object with the new values.
     * @return The updated book.
     * @throws IllegalStateException if the book to update is not found.
     */
    public Book updateBook(String isbn, Book updatedBook) {
        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalStateException("Book with ISBN " + isbn + " not found."));
                
        // Only update fields that are not null in the request body
        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            existingBook.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getStock() != null) {
            existingBook.setStock(updatedBook.getStock());
        }
        
        // Save the updated book to the database
        return bookRepository.save(existingBook);
    }

    /**
     * Deletes a book from the database.
     * @param isbn The ISBN of the book to delete.
     * @return true if the book was deleted, false otherwise.
     */
    public boolean deleteBookByIsbn(String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return true;
        }
        return false;
    }
}
