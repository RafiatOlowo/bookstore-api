package com.rafiatolowo.bookstore_api.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book book1, book2, book3;

    @BeforeEach
    public void setup() {
        // Clear the database and persist new test data before each test
        entityManager.getEntityManager().createQuery("DELETE FROM Book").executeUpdate();

        book1 = new PhysicalCopyBook("9780743273565", "The Great Gatsby", "F. Scott Fitzgerald", 100);
        book2 = new PhysicalCopyBook("9780441172719", "Dune", "Frank Herbert", 50);
        book3 = new EBook("9780451524935", "1984", "George Orwell", 200);

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);

        entityManager.flush();
    }

    @Test
    void findByIsbn_shouldReturnBook() {
        Optional<Book> foundBook = bookRepository.findByIsbn("9780441172719");
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getIsbn()).isEqualTo("9780441172719");
    }

    @Test
    void findByIsbn_shouldReturnEmptyOptional_forNonExistingIsbn() {
        Optional<Book> foundBook = bookRepository.findByIsbn("non-existent-isbn");
        assertThat(foundBook).isEmpty();
    }

    @Test
    void findByAuthor_shouldReturnCorrectBooks() {
        // Create an additional book by the same author to test for multiple results
        Book book4 = new PhysicalCopyBook("9780441172726", "Book by Frank Herbert", "Frank Herbert", 75);
        entityManager.persist(book4);
        entityManager.flush();

        List<Book> booksByAuthor = bookRepository.findByAuthor("Frank Herbert");
        assertThat(booksByAuthor).hasSize(2);
        assertThat(booksByAuthor.get(0).getAuthor()).isEqualTo("Frank Herbert");
        assertThat(booksByAuthor.get(1).getAuthor()).isEqualTo("Frank Herbert");
    }

    @Test
    void findAllPhysicalCopyBooks_shouldReturnOnlyPhysicalBooks() {
        List<PhysicalCopyBook> physicalBooks = bookRepository.findAllPhysicalCopyBooks();
        assertThat(physicalBooks).hasSize(2); // "The Great Gatsby" and "Dune"
    }

    @Test
    void findAllEBooks_shouldReturnOnlyEbooks() {
        List<EBook> eBooks = bookRepository.findAllEBooks();
        assertThat(eBooks).hasSize(1); // "1984"
        assertThat(eBooks.get(0).getTitle()).isEqualTo("1984");
    }

    /**
     * Test case to ensure that a book can be successfully retrieved from the database by its ID.
     * This validates that the entity was correctly persisted and its primary key is functional.
     */
    @Test
    void findById_shouldReturnCorrectBook() {
        Optional<Book> foundBook = bookRepository.findById(book1.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getIsbn()).isEqualTo("9780743273565");
    }
}
