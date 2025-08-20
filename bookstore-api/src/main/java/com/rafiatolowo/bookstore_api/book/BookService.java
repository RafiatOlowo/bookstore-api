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
     * @param isbn The ISBN of the book.
     * @return An Optional containing the book, or empty if not found.
     */
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(bookRepository.findByIsbn(isbn));
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
            throw new IllegalStateException("A book with this ISBN already exists.");
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
     * Updates an existing book.
     * @param isbn The ISBN of the book to update.
     * @param updatedBook The book object with updated details.
     * @return The updated book.
     * @throws IllegalStateException if the book with the given ISBN is not found.
     */
    public Book updateBook(String isbn, Book updatedBook) {
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook != null) {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setStock(updatedBook.getStock());
            return bookRepository.save(existingBook);
        } else {
            throw new IllegalStateException("Book with ISBN " + isbn + " not found.");
        }
    }

    /**
     * Deletes a book from the database.
     * @param isbn The ISBN of the book to delete.
     * @return true if the book was deleted, false otherwise.
     */
    public boolean deleteBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            bookRepository.delete(book);
            return true;
        }
        return false;
    }
}
