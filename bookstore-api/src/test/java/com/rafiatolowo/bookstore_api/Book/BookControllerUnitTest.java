package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    // We now mock the BookService, not the BookRepository
    @MockBean
    private BookService bookService;

    @Test
    void testGetAllBooks() throws Exception {
        // Arrange: Create a fake list of books that the mock service will return.
        // We now use a concrete class like PaperbackBook instead of the abstract Book class.
        PaperbackBook book1 = new PaperbackBook("978-0134685991", "Clean Code", "Robert C. Martin", 100);
        PaperbackBook book2 = new PaperbackBook("978-0134685992", "The Clean Coder", "Robert C. Martin", 80);
        List<Book> allBooks = Arrays.asList(book1, book2);

        // Tell the mock service what to return when its getAllBooks method is called
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // Act & Assert: Perform a GET request to the /api/books endpoint and verify the response
        mockMvc.perform(get("/api/books"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].title").value("Clean Code"))
               .andExpect(jsonPath("$[1].title").value("The Clean Coder"));
    }

    @Test
    void testGetBooksByAuthor() throws Exception {
        // Arrange: Create a fake list of books for a specific author using the concrete class
        PaperbackBook book1 = new PaperbackBook("978-0134685991", "Clean Code", "Robert C. Martin", 100);
        PaperbackBook book2 = new PaperbackBook("978-0134685992", "The Clean Coder", "Robert C. Martin", 80);
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
    void testAddBookSuccess() throws Exception {
        // Arrange: Create a sample book to add using the concrete class
        PaperbackBook newBook = new PaperbackBook("978-1234567890", "Test Driven Development", "Kent Beck", 50);
        
        // Tell the mock service to return the book when addBook is called
        when(bookService.addBook(any(Book.class))).thenReturn(newBook);

        // Act & Assert: Perform a POST request and verify the response.
        // We must include the "bookType" field in the JSON payload
        // to correctly deserialize the JSON into the specific PaperbackBook class.
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\":\"978-1234567890\", \"title\":\"Test Driven Development\", \"author\":\"Kent Beck\", \"stock\":50, \"bookType\":\"paperback\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Driven Development"));
    }
}
