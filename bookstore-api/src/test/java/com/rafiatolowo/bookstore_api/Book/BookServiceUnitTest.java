package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the BookService class using Mockito.
 * This class isolates the business logic of the service and tests it
 * independently of the controller and repository layers.
 */
@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    // @Mock creates a mock instance of the BookRepository.
    @Mock
    private BookRepository bookRepository;

    // @InjectMocks injects the mock repository into the BookService instance.
    @InjectMocks
    private BookService bookService;

    @Test
    void testGetAllBooks() {
        // Arrange: Create some mock books and tell the mock repository to return them.
        // This test includes both EBook and PhysicalCopyBook.
        EBook book1 = new EBook("123", "Title 1", "Author 1", 100);
        PhysicalCopyBook book2 = new PhysicalCopyBook("456", "Title 2", "Author 2", 200);
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(books);

        // Act: Call the service method.
        List<Book> result = bookService.getAllBooks();

        // Assert: Verify the result is as expected and the repository method was called.
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).getTitle());
        assertEquals("Title 2", result.get(1).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

     @Test
    void testFindEBookByIsbnFound() {
        // Arrange: Create a mock EBook and tell the mock repository to return it.
        String isbn = "123";
        EBook book = new EBook(isbn, "Title 1", "Author 1", 100);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        // Act: Call the service method.
        Optional<Book> result = bookService.findByIsbn(isbn);

        // Assert: Verify the book is found and the repository method was called.
        assertTrue(result.isPresent());
        assertEquals(isbn, result.get().getIsbn());
        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    void testFindPhysicalCopyBookByIsbnFound() {
        // Arrange: Create a mock book and tell the mock repository to return it.
        String isbn = "123-1";
        PhysicalCopyBook book = new PhysicalCopyBook(isbn, "Title 1", "Author 1", 100);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        // Act: Call the service method.
        Optional<Book> result = bookService.findByIsbn(isbn);

        // Assert: Verify the book is found and the repository method was called.
        assertTrue(result.isPresent());
        assertEquals(isbn, result.get().getIsbn());
        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    void testFindBookByIsbnNotFound() {
        // Arrange: Tell the mock repository to return an empty Optional.
        String isbn = "999";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // Act: Call the service method.
        Optional<Book> result = bookService.findByIsbn(isbn);

        // Assert: Verify that no book is found.
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    void testAddEBookSuccess() {
        // Arrange: Create a new book and set up the mocks.
        EBook newBook = new EBook("123", "New Book", "New Author", 50);
        when(bookRepository.findByIsbn("123")).thenReturn(Optional.empty()); // No existing book found
        when(bookRepository.save(newBook)).thenReturn(newBook); // Return the saved book

        // Act: Call the service method.
        Book result = bookService.addBook(newBook);

        // Assert: Verify the book was saved and the result is correct.
        assertEquals(newBook, result);
        verify(bookRepository, times(1)).findByIsbn("123");
        verify(bookRepository, times(1)).save(newBook);
    }

    @Test
    void testAddPhysicalCopyBookSuccess() {
        // Arrange: Create a new PhysicalCopyBook and set up the mocks.
        PhysicalCopyBook newBook = new PhysicalCopyBook("456", "New Physical Book", "Another Author", 100);
        when(bookRepository.findByIsbn("456")).thenReturn(Optional.empty()); // No existing book found
        when(bookRepository.save(newBook)).thenReturn(newBook); // Return the saved book

        // Act: Call the service method.
        Book result = bookService.addBook(newBook);

        // Assert: Verify the book was saved and the result is correct.
        assertEquals(newBook, result);
        verify(bookRepository, times(1)).findByIsbn("456");
        verify(bookRepository, times(1)).save(newBook);
    }

    @Test
    void testAddBookConflict() {
        // Arrange: Create a book that already exists and set up the mocks.
        EBook existingBook = new EBook("123", "Existing Book", "Old Author", 10);
        when(bookRepository.findByIsbn("123")).thenReturn(Optional.of(existingBook)); // Existing book is found

        // Act & Assert: Verify that an IllegalStateException is thrown.
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.addBook(existingBook);
        });

        // Assert: Verify the exception message and that the save method was never called.
        assertEquals("A book with ISBN 123 already exists.", exception.getMessage());
        verify(bookRepository, times(1)).findByIsbn("123");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testFindBooksByAuthor() {
        // Arrange: Create some mock books for the same author.
        EBook book1 = new EBook("123", "Title 1", "Author A", 100);
        PhysicalCopyBook book2 = new PhysicalCopyBook("456", "Title 2", "Author A", 200);
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findByAuthor("Author A")).thenReturn(books);

        // Act: Call the service method.
        List<Book> result = bookService.findBooksByAuthor("Author A");

        // Assert: Verify the correct number of books and that the repository method was called.
        assertEquals(2, result.size());
        assertEquals("Author A", result.get(0).getAuthor());
        assertEquals("Author A", result.get(1).getAuthor());
        verify(bookRepository, times(1)).findByAuthor("Author A");
    }

    @Test
    void testUpdateEBookSuccess() {
        // Arrange: Create an existing book and a book with updated data.
        String isbn = "123";
        EBook existingBook = new EBook(isbn, "Old Title", "Old Author", 50);
        EBook updatedBookData = new EBook(null, "New Title", "New Author", 100);

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook)); // Find the existing book
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved book

        // Act: Call the service method.
        Book result = bookService.updateBook(isbn, updatedBookData);

        // Assert: Verify the book was updated with the new data.
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(100, result.getStock());
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

     @Test
    void testUpdatePhysicalCopyBookSuccess() {
        // Arrange: Create an existing book and a book with updated data.
        String isbn = "123";
        PhysicalCopyBook existingBook = new PhysicalCopyBook(isbn, "Old Title", "Old Author", 50);
        PhysicalCopyBook updatedBookData = new PhysicalCopyBook(null, "New Title", "New Author", 100);

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook)); // Find the existing book
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved book

        // Act: Call the service method.
        Book result = bookService.updateBook(isbn, updatedBookData);

        // Assert: Verify the book was updated with the new data.
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(100, result.getStock());
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBookNotFound() {
        // Arrange: Set up the mock to return an empty Optional.
        String isbn = "999";
        EBook updatedBookData = new EBook(null, "New Title", "New Author", 100);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // Act & Assert: Verify that an IllegalStateException is thrown.
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.updateBook(isbn, updatedBookData);
        });

        // Assert: Verify the exception message.
        assertEquals("Book with ISBN " + isbn + " not found.", exception.getMessage());
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteEBookSuccess() {
        // Arrange: Set up the mock to find a book to delete.
        String isbn = "123";
        EBook existingBook = new EBook(isbn, "Title", "Author", 10);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook));

        // Act: Call the service method.
        boolean result = bookService.deleteBookByIsbn(isbn);

        // Assert: Verify the deletion was successful and the repository's delete method was called.
        assertTrue(result);
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, times(1)).delete(existingBook);
    }

    @Test
    void testDeletePhysicalCopyBookSuccess() {
        // Arrange: Set up the mock to find a PhysicalCopyBook to delete.
        String isbn = "456";
        PhysicalCopyBook existingBook = new PhysicalCopyBook(isbn, "Title", "Author", 10);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook));

        // Act: Call the service method.
        boolean result = bookService.deleteBookByIsbn(isbn);

        // Assert: Verify the deletion was successful and the repository's delete method was called.
        assertTrue(result);
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, times(1)).delete(existingBook);
    }

    @Test
    void testDeleteBookNotFound() {
        // Arrange: Set up the mock to return an empty Optional.
        String isbn = "999";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // Act: Call the service method.
        boolean result = bookService.deleteBookByIsbn(isbn);

        // Assert: Verify the deletion was not successful and the repository's delete method was not called.
        assertFalse(result);
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    // Tests for Invalid Input

    @Test
    void findByIsbn_withNullIsbn_throwsIllegalArgumentException() {
        // Assert that the service method throws IllegalArgumentException when a null ISBN is provided.
        assertThrows(IllegalArgumentException.class, () -> bookService.findByIsbn(null));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).findByIsbn(any());
    }

    @Test
    void findByIsbn_withEmptyIsbn_throwsIllegalArgumentException() {
        // Assert that the service method throws IllegalArgumentException when an empty ISBN is provided.
        assertThrows(IllegalArgumentException.class, () -> bookService.findByIsbn(""));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).findByIsbn(any());
    }

    @Test
    void addBook_withNullBook_throwsIllegalArgumentException() {
        // Assert that the service method throws IllegalArgumentException when a null Book is provided.
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(null));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void addBook_withNullIsbn_throwsIllegalArgumentException() {
        // Arrange: Create a book with a null ISBN.
        EBook bookWithNullIsbn = new EBook(null, "Test Title", "Test Author", 10);
        
        // Assert that the service method throws IllegalArgumentException.
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(bookWithNullIsbn));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void findBooksByAuthor_withNullAuthor_throwsIllegalArgumentException() {
        // Assert that the service method throws IllegalArgumentException when a null author is provided.
        assertThrows(IllegalArgumentException.class, () -> bookService.findBooksByAuthor(null));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).findByAuthor(any());
    }

    @Test
    void updateBook_withNullIsbn_throwsIllegalArgumentException() {
        // Arrange: Create a book object for the update.
        EBook updatedBookData = new EBook(null, "New Title", "New Author", 100);
        
        // Assert that the service method throws IllegalArgumentException when a null ISBN is provided.
        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(null, updatedBookData));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).findByIsbn(any());
    }

    @Test
    void deleteBookByIsbn_withNullIsbn_throwsIllegalArgumentException() {
        // Assert that the service method throws IllegalArgumentException when a null ISBN is provided.
        assertThrows(IllegalArgumentException.class, () -> bookService.deleteBookByIsbn(null));
        // Verify that the repository method was never called.
        verify(bookRepository, never()).findByIsbn(any());
    }
}
