package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

/**
 * Integration tests for the BookService. These tests verify the service's
 * behavior by making real network calls to the Google Books API.
 * NOTE: These tests require a working internet connection.
 */
@SpringBootTest
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        // Clear the repository before each test to ensure a clean state
        bookRepository.deleteAll();
    }

    @Test
    void testFindByIsbn_withNonExistentIsbn_usesGoogleBooksApiAndSavesNewBook() {
        // Arrange with a valid ISBN that is not in our local database
        String isbn = "9780134685991"; // A valid ISBN for "Effective Java"

        // Act: Call the service method, which should now fall back to the API
        Optional<Book> result = bookService.findByIsbn(isbn);

        // Assert: Verify that a book was found, and the stock was correctly set to 0.
        assertTrue(result.isPresent());
        assertEquals(isbn, result.get().getIsbn());
        assertEquals(0, result.get().getStock());
        
        // Assert that the book was also saved to the local database
        assertTrue(bookRepository.findByIsbn(isbn).isPresent());
    }

    @Test
    void testFindByIsbn_usesGoogleBooksApi_returnsNoItems() {
        // Arrange with an ISBN that is very unlikely to exist on Google Books API
        String isbn = "9999999999999";

        // Act: Call the service method
        Optional<Book> result = bookService.findByIsbn(isbn);

        // Assert: Verify that no book was found
        assertTrue(result.isEmpty());
        
        // Verify that nothing was saved to the local database
        assertTrue(bookRepository.findByIsbn(isbn).isEmpty());
    }
}
