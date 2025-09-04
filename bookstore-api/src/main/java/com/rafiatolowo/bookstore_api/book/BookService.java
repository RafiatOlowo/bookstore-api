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
     *
     * @param isbn The ISBN of the book to find.
     * @return An Optional containing the found book, or an empty Optional if not found.
     * @throws IllegalArgumentException if the ISBN is null or empty.
     */
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN must not be null or empty");
        }
        return bookRepository.findByIsbn(isbn);
    }
    
    /**
     * Adds a new book to the database.
     * For example, it checks if a book with the same ISBN already exists
     * before saving a new one.
     * 
     * @param book The book object to save.
     * @return The saved book entity.
     * @throws IllegalStateException if a book with the same ISBN already exists.
     * @throws IllegalArgumentException if the provided book is null or has a null/empty ISBN.
     */
    public Book addBook(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("Book or its ISBN cannot be null or empty.");
        }
        
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
     * @throws IllegalArgumentException if the author is null or empty.
     */
    public List<Book> findBooksByAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        return bookRepository.findByAuthor(author);
    }

     /**
     * Updates an existing book. This method handles partial updates by
     * only updating the fields that are not null in the request body.
     * 
     * @param isbn The ISBN of the book to update.
     * @param updatedBook The book object with the new values.
     * @return The updated book.
     * @throws IllegalStateException if the book to update is not found.
     * @throws IllegalArgumentException if the ISBN is null or empty, or if an
     * attempt is made to update immutable fields like ID or bookType.
     */
    public Book updateBook(String isbn, Book updatedBook) {
       // Validation: Check for invalid input first
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalStateException("Book with ISBN " + isbn + " not found."));
        
        // Validate that immutable fields are not being updated.
        // The ID is generated, and the book's type is part of its identity,
        // so neither should be changeable.
        if (updatedBook.getId() != null) {
            throw new IllegalArgumentException("Book ID cannot be updated.");
        }

        // Used getClass() to prevent changing the book type.
        // This is because bookType is a JPA discriminator, not a field.
        if (!existingBook.getClass().equals(updatedBook.getClass())) {
            throw new IllegalArgumentException("Book type cannot be updated.");
        }
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
     * 
     * @param isbn The ISBN of the book to delete.
     * @return true if the book was deleted, false otherwise.
     * @throws IllegalArgumentException if the ISBN is null or empty.
     */
    public boolean deleteBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return true;
        }
        return false;
    }
}
