package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Description;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import com.rafiatolowo.bookstore_api.BookstoreApiApplication;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the BookController class.
 */

@SpringBootTest(classes = BookstoreApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private URI baseUri;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private BookService bookService;

    private List<Book> defaultBooks = new ArrayList<>();

    @BeforeEach
    void setUp() {
        this.baseUri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("api/books")
                .build()
                .toUri();

        // Some sample data for the mock service
        defaultBooks.add(new EBook("978-0135957059", "The Pragmatic Programmer", "Andrew Hunt, David Thomas", 100));
        defaultBooks.add(new PhysicalCopyBook("978-0441172719", "Dune", "Frank Herbert", 50));
        when(bookService.getAllBooks()).thenReturn(defaultBooks);
    }

    @Test
    @Description("GET /api/books Test for getting all books")
    void testGetAllBooks_returnsAllBooks() {
        // Act
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                baseUri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {});
        List<Book> responseBooks = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBooks);
        assertEquals(defaultBooks.size(), responseBooks.size());
        verify(bookService).getAllBooks();
    }

    @Test
    void testAddBook_returnsCreatedBook() {
        // Arrange
        Book newBook = new EBook("978-1234567890", "Test Title", "Test Author", 10);
        when(bookService.addBook(any(Book.class))).thenReturn(newBook);

        // Act
        ResponseEntity<Book> response = restTemplate.postForEntity(baseUri, newBook, Book.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newBook.getIsbn(), response.getBody().getIsbn());
        verify(bookService).addBook(any(Book.class));
    }

    @Test
    void testAddBook_returnsConflictWhenBookAlreadyExists() {
        // Arrange
        Book existingBook = new EBook("978-0135957059", "The Pragmatic Programmer", "Andrew Hunt, David Thomas", 100);
        when(bookService.addBook(any(Book.class))).thenThrow(new IllegalStateException("Book already exists"));

        // Act
        ResponseEntity<Void> response = restTemplate.postForEntity(baseUri, existingBook, Void.class);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(bookService).addBook(any(Book.class));
    }

    @Test
    void testGetBookByIsbn_returnsBook() {
        // Arrange
        String isbn = defaultBooks.get(0).getIsbn();
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.of(defaultBooks.get(0)));
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment(isbn).build().toUri();

        // Act
        ResponseEntity<Book> response = restTemplate.getForEntity(endpoint, Book.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(isbn, response.getBody().getIsbn());
        verify(bookService).findByIsbn(isbn);
    }

    @Test
    void testGetBookByIsbn_returnsNotFound() {
        // Arrange
        String isbn = "non-existent-isbn";
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment(isbn).build().toUri();

        // Act
        ResponseEntity<Void> response = restTemplate.getForEntity(endpoint, Void.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService).findByIsbn(isbn);
    }

    @Test
    void testGetBooksByAuthor_returnsBooks() {
        // Arrange
        String author = "Frank Herbert";
        List<Book> duneBooks = new ArrayList<>();
        duneBooks.add(defaultBooks.get(1));

        when(bookService.findBooksByAuthor(author)).thenReturn(duneBooks);
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment("author", author).build().toUri();

        // Act
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {});
        List<Book> responseBooks = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBooks);
        assertEquals(1, responseBooks.size());
        assertEquals(author, responseBooks.get(0).getAuthor());
        verify(bookService).findBooksByAuthor(author);
    }

    @Test
    void testUpdateBook_returnsUpdatedBook() {
        // Arrange
        String isbn = defaultBooks.get(0).getIsbn();
        Book updatedBookData = new PhysicalCopyBook(isbn, "Updated Title", "Updated Author", 50);

        when(bookService.updateBook(eq(isbn), any(Book.class))).thenReturn(updatedBookData);
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment(isbn).build().toUri();

        // Act
        ResponseEntity<Book> response = restTemplate.exchange(endpoint, HttpMethod.PATCH, new HttpEntity<>(updatedBookData), Book.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Title", response.getBody().getTitle());
        verify(bookService).updateBook(eq(isbn), any(Book.class));
    }

    @Test
    void testUpdateBook_returnsNotFoundForNonExistentBook() {
        // Arrange
        String isbn = "non-existent-isbn";
        Book updatedBookData = new EBook(isbn, "Updated Title", "Updated Author", 50);

        when(bookService.updateBook(eq(isbn), any(Book.class))).thenThrow(new IllegalStateException("Book not found"));
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment(isbn).build().toUri();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(endpoint, HttpMethod.PATCH, new HttpEntity<>(updatedBookData), Void.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService).updateBook(eq(isbn), any(Book.class));
    }

    @Test
    void testDeleteBook_returnsNoContent() {
        // Arrange
        String isbn = defaultBooks.get(0).getIsbn();
        when(bookService.deleteBookByIsbn(isbn)).thenReturn(true);
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment(isbn).build().toUri();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(endpoint, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService).deleteBookByIsbn(isbn);
    }

    @Test
    void testDeleteBook_returnsNotFoundForNonExistentBook() {
        // Arrange
        String isbn = "non-existent-isbn";
        when(bookService.deleteBookByIsbn(isbn)).thenReturn(false);
        URI endpoint = UriComponentsBuilder.fromUri(baseUri).pathSegment(isbn).build().toUri();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(endpoint, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService).deleteBookByIsbn(isbn);
    }
}
