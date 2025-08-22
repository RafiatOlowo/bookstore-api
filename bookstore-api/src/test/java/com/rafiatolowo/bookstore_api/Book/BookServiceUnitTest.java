package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void testAddBookSuccess() {
        // Arrange: Create a new concrete book (PaperbackBook) since Book is now abstract
        PaperbackBook newBook = new PaperbackBook("978-1234567890", "Test Driven Development", "Kent Beck", 50);

        // Tell the mock repository what to return when findByIsbn is called (simulating no duplicate)
        // We now return an empty Optional because the repository method returns Optional<Book>
        when(bookRepository.findByIsbn(newBook.getIsbn())).thenReturn(Optional.empty());

        // Tell the mock repository what to return when save is called
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        // Act
        Book savedBook = bookService.addBook(newBook);

        // Assert
        assertThat(savedBook).isEqualTo(newBook);
        verify(bookRepository, times(1)).save(newBook); // Verify that save was called exactly once
    }

    @Test
    void testAddBookThrowsExceptionForDuplicate() {
        // Arrange: Create a book that already exists
        PaperbackBook existingBook = new PaperbackBook("978-1234567890", "Duplicate Book", "Jane Doe", 20);

        // Tell the mock repository to return an Optional containing the existing book
        // when findByIsbn is called.
        when(bookRepository.findByIsbn(existingBook.getIsbn())).thenReturn(Optional.of(existingBook));

        // Act & Assert: Use assertThatThrownBy to verify that an exception is thrown
        assertThatThrownBy(() -> bookService.addBook(existingBook))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A book with ISBN " + existingBook.getIsbn() + " already exists.");

        // Verify that the save method was never called
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    void testDeleteBookByIsbnSuccess() {
        // Arrange
        String isbn = "978-1234567890";
        PaperbackBook bookToDelete = new PaperbackBook(isbn, "Book to Delete", "John Smith", 10);
        
        // The mock now needs to return an Optional containing the book to delete.
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookToDelete));

        // Act
        boolean result = bookService.deleteBookByIsbn(isbn);

        // Assert
        assertThat(result).isTrue();
        verify(bookRepository, times(1)).delete(bookToDelete);
    }

    @Test
    void testDeleteBookByIsbnNotFound() {
        // Arrange
        String isbn = "non-existent-isbn";
        // The mock should return an empty Optional when the book is not found.
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // Act
        boolean result = bookService.deleteBookByIsbn(isbn);

        // Assert
        assertThat(result).isFalse();
        // Verify that the delete method was never called, since the book wasn't found.
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
