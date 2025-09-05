package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the BookController class using MockMvc.
 * These tests focus only on the controller's logic, without starting the full application.
 */
@WebMvcTest(BookController.class)
public class BookControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void testDeleteBookByIsbn_notFound_returnsNotFound() throws Exception {
        // Arrange
        String isbn = "978-9999999999";
        String expectedMessage = "Book not found with ISBN: " + isbn;
        when(bookService.deleteBookByIsbn(isbn)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/books/{isbn}", isbn))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void testDeleteBookByIsbn_success_returnsNoContent() throws Exception {
        // Arrange
        String isbn = "978-0321765723";
        when(bookService.deleteBookByIsbn(isbn)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/books/{isbn}", isbn))
                .andExpect(status().isNoContent());
    }
}
