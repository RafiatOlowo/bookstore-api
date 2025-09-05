package com.rafiatolowo.bookstore_api.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    
    // ObjectMapper is used to convert Java objects to JSON strings
    @Autowired
    private ObjectMapper objectMapper;

    // This test verifies a successful GET request for all book types.
    @Test
    void testGetAllBooks() throws Exception {
        // Arrange: Create a fake list of books that the mock service will return.
        EBook book1 = new EBook("978-0134685991", "Clean Code", "Robert C. Martin", 100);
        PhysicalCopyBook book2 = new PhysicalCopyBook("978-0134685992", "The Clean Coder", "Robert C. Martin", 80);
        List<Book> allBooks = Arrays.asList(book1, book2);

        // Tell the mock service what to return when its getAllBooks method is called
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // Act: Perform a GET request to the controller.
        mockMvc.perform(get("/api/books"))
               .andExpect(status().isOk())
               // Assert: Verify the content of the JSON array.
                // The order might not be guaranteed, so it's safer to check for existence and properties.
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].bookType").value("ebook")) // Verify the type of the first book.
                .andExpect(jsonPath("$[1].title").value("The Clean Coder"))
                .andExpect(jsonPath("$[1].bookType").value("physical_copy")); // Verify the type of the second book.
    }

   
    // This test verifies a successful POST request to add an Ebook.
    @Test
    void testAddEBookSuccess() throws Exception {
        // Arrange: Create a sample EBook to add.
        EBook newEbook = new EBook("978-1234567890", "Test Driven Development", "Kent Beck", 50);
        
        // Mock the service to return the EBook when addBook is called.
        when(bookService.addBook(any(Book.class))).thenReturn(newEbook);

        // Act & Assert: Perform a POST request and verify the response.
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEbook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Driven Development"))
                .andExpect(jsonPath("$.bookType").value("ebook"));
    }

    // This test verifies a successful POST request for a PhysicalCopyBook.
    @Test
    void testAddPhysicalCopyBookSuccess() throws Exception {
        // Arrange: Create a sample PhysicalCopyBook to add.
        PhysicalCopyBook newPhysicalBook = new PhysicalCopyBook("978-0201485677", "The Mythical Man-Month", "Frederick Brooks Jr.", 75);
        
        // Mock the service to return the PhysicalCopyBook when addBook is called.
        when(bookService.addBook(any(Book.class))).thenReturn(newPhysicalBook);

        // Act & Assert: Perform a POST request and verify the response.
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPhysicalBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Mythical Man-Month"))
                .andExpect(jsonPath("$.bookType").value("physical_copy"));
    }

    // This test verifies that the API returns a conflict status when a duplicate book is added.
    @Test
    void testAddBookConflict() throws Exception {
        // Arrange: Create a book that already exists
        EBook existingBook = new EBook("978-1234567890", "Duplicate Book", "Jane Doe", 20);

        // Tell the mock service to throw an exception when addBook is called with a duplicate ISBN
        when(bookService.addBook(any(Book.class))).thenThrow(new IllegalStateException("A book with this ISBN already exists."));

        // Act & Assert: Perform a POST request and verify a CONFLICT status is returned
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingBook)))
                .andExpect(status().isConflict());
    }
    
    @Test
    void testGetBookByIsbnSuccess() throws Exception {
        // Arrange: Create a book that the service will return
        EBook foundBook = new EBook("978-0134685991", "Clean Code", "Robert C. Martin", 100);

        // Tell the mock service to return the book when findByIsbn is called
        when(bookService.findByIsbn("978-0134685991")).thenReturn(Optional.of(foundBook));
        
        // Act & Assert: Perform a GET request and verify the response
        mockMvc.perform(get("/api/books/{isbn}", "978-0134685991"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Clean Code"));
    }
    
    @Test
    void testGetBookByIsbnNotFound() throws Exception {
        // Arrange: Tell the mock service to return an empty Optional
        when(bookService.findByIsbn("978-1234567890")).thenReturn(Optional.empty());
        
        // Act & Assert: Perform a GET request and verify a NOT_FOUND status
        mockMvc.perform(get("/api/books/{isbn}", "978-1234567890"))
               .andExpect(status().isNotFound());
    }

    @Test
    void testGetBooksByAuthor() throws Exception {
        // Arrange: Create a fake list of books for a specific author using the concrete class
        EBook book1 = new EBook("978-0134685991", "Clean Code", "Robert C. Martin", 100);
        EBook book2 = new EBook("978-0134685992", "The Clean Coder", "Robert C. Martin", 80);
        List<Book> books = Arrays.asList(book1, book2);

        // Tell the mock service what to return when its findBooksByAuthor method is called
        when(bookService.findBooksByAuthor("Robert C. Martin")).thenReturn(books);

        // Act & Assert: Perform a GET request to the endpoint and verify the response
        mockMvc.perform(get("/api/books/author/{author}", "Robert C. Martin"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].author").value("Robert C. Martin"))
               .andExpect(jsonPath("$[1].author").value("Robert C. Martin"));
    }
    
    @Test
    void testUpdateBookSuccess() throws Exception {
        // Arrange: Create the updated version of the book
        EBook updatedBook = new EBook("978-0134685991", "Updated Title", "Robert C. Martin", 150);

        // Tell the mock service to return the updated book
        when(bookService.updateBook(any(String.class), any(Book.class))).thenReturn(updatedBook);
        
        // Act & Assert: Perform a PATCH request and verify the response
        mockMvc.perform(patch("/api/books/{isbn}", "978-0134685991")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testUpdateBookNotFound() throws Exception {
        // Arrange: Create a book to update that does not exist
        EBook nonExistentBook = new EBook("978-9999999999", "No Such Book", "Unknown", 0);

        // Tell the mock service to throw an exception when the book is not found
        when(bookService.updateBook(any(String.class), any(Book.class))).thenThrow(new IllegalStateException());

        // Act & Assert: Perform a PATCH request and verify a NOT_FOUND status is returned
        mockMvc.perform(patch("/api/books/{isbn}", "978-9999999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBookSuccess() throws Exception {
        // Arrange: Tell the mock service that the deletion will be successful
        when(bookService.deleteBookByIsbn("978-0134685991")).thenReturn(true);

        // Act & Assert: Perform a DELETE request and verify the NO_CONTENT status
        mockMvc.perform(delete("/api/books/{isbn}", "978-0134685991"))
                .andExpect(status().isNoContent());

        // Verify that the service method was called exactly once
        verify(bookService, times(1)).deleteBookByIsbn("978-0134685991");
    }

    @Test
    void testDeleteBookNotFound() throws Exception {
        // Arrange: Tell the mock service that the book does not exist
        when(bookService.deleteBookByIsbn("978-9999999999")).thenReturn(false);

        // Act & Assert: Perform a DELETE request and verify the NOT_FOUND status
        mockMvc.perform(delete("/api/books/{isbn}", "978-9999999999"))
                .andExpect(status().isNotFound());
    }
}
