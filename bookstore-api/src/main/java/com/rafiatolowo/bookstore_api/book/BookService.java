package com.rafiatolowo.bookstore_api.book;

import org.springframework.stereotype.Service;

import com.rafiatolowo.bookstore_api.book.exceptions.BookAlreadyExistsException;
import com.rafiatolowo.bookstore_api.book.exceptions.BookNotFoundException;

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
     * Retrieves a book from the repository using its unique ISBN.
     *
     * This method searches for a book based on the provided ISBN. If a book
     * with the matching ISBN is found, it is returned within an {@code Optional}.
     * If no book is found for the given ISBN, an empty {@code Optional} is returned.
     *
     * @param isbn The ISBN (International Standard Book Number) of the book to search for.
     * This parameter must not be null or an empty string after trimming whitespace.
     * @return An {@code Optional<Book>} containing the {@code Book} object if found,
     * otherwise an empty {@code Optional}.
     * @throws IllegalArgumentException if the provided {@code isbn} is null, empty, or
     * consists only of whitespace.
     */
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN must not be null or empty");
        }
        return bookRepository.findByIsbn(isbn);
    }
    
      /**
     * Adds a new book to the database.
     * <p>
     * This method first validates that the provided {@code book} object and its ISBN
     * are not null or empty. It then checks if a book with the same ISBN
     * already exists in the database. If a duplicate is found, a
     * {@link BookAlreadyExistsException} is thrown. Otherwise, the new book
     * is saved and returned.
     * </p>
     *
     * @param book The {@code Book} object to be added to the database. Must not be null and must contain a valid ISBN.
     * @return The {@code Book} entity as it is saved in the database, including any generated IDs.
     * @throws IllegalArgumentException if the provided {@code book} is null or if its ISBN is null or blank.
     * @throws BookAlreadyExistsException if a book with the same ISBN already exists in the repository.
     */
    public Book addBook(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("Book or its ISBN cannot be null or empty.");
        }
        
        // Business logic: Check for duplicates before saving.
        Optional<Book> existingBook = findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException("A book with ISBN " + book.getIsbn() + " already exists.");
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
     * only updating the fields that are not null in the provided {@code updatedBook} object.
     * The update is performed on the book identified by the given ISBN.
     *
     * @param isbn The ISBN of the book to update. Must not be null or empty.
     * @param updatedBook The book object containing the new values for the fields to be updated.
     * Fields with a null value will be ignored.
     * @return The fully updated {@code Book} object as it is saved in the database.
     * @throws IllegalArgumentException if the {@code isbn} is null or empty, or if an
     * attempt is made to update immutable fields such as the book's ID or its type.
     * @throws BookNotFoundException if a book with the specified {@code isbn} is not found in the repository.
     */
    public Book updateBook(String isbn, Book updatedBook) {
       // Validation: Check for invalid input first
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found."));
        
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
     * @throws BookNotFoundException if the book to delete is not found.
     */
    public boolean deleteBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return true;
        } else {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found.");
        }
    }
}
