package com.rafiatolowo.bookstore_api.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rafiatolowo.bookstore_api.book.Book;
import com.rafiatolowo.bookstore_api.book.BookController;
import com.rafiatolowo.bookstore_api.book.BookRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerUnitTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void testGetBooksByAuthor() throws Exception {
        // Arrange
        // Create a fake list of books that the mock repository will return
        Book book1 = new Book("978-0134685991", "Clean Code", "Robert C. Martin", 100);
        Book book2 = new Book("978-0134685992", "The Clean Coder", "Robert C. Martin", 80);
        List<Book> books = Arrays.asList(book1, book2);

        // Tell the mock repository what to return when its findByAuthor method is called
        when(bookRepository.findByAuthor("Robert C. Martin")).thenReturn(books);

        // Act & Assert
        // Perform a GET request to the endpoint and verify the response
        mockMvc.perform(get("/api/books/author/{author}", "Robert C. Martin"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].author").value("Robert C. Martin"))
               .andExpect(jsonPath("$[1].author").value("Robert C. Martin"));
    }

    @Test
    void testGetAllBooks() throws Exception {
        // Arrange
        // Create a fake list of all books
        Book book1 = new Book("978-0134685991", "Clean Code", "Robert C. Martin", 100);
        Book book2 = new Book("978-0134685992", "The Clean Coder", "Robert C. Martin", 80);
        List<Book> allBooks = Arrays.asList(book1, book2);

        // Tell the mock repository what to return when its findAll method is called
        when(bookRepository.findAll()).thenReturn(allBooks);

        // Act & Assert
        // Perform a GET request to the /api/books endpoint and verify the response
        mockMvc.perform(get("/api/books"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].title").value("Clean Code"))
               .andExpect(jsonPath("$[1].title").value("The Clean Coder"));
    }
}
