package com.rafiatolowo.bookstore_api.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * The repository interface for Book entities.
 * It extends JpaRepository to inherit standard CRUD operations.
 * You can define custom query methods here by simply naming them correctly.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Finds a book by its unique ISBN.
     * @param isbn The ISBN of the book.
     * @return The book with the specified ISBN.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Finds a list of books by a specific author.
     * This is a derived query method, where Spring Data JPA
     * automatically generates the SQL query from the method name.
     * @param author The name of the author to search for.
     * @return A list of books by the specified author.
     */
    List<Book> findByAuthor(String author);

    /**
     * Finds all books that are of type PhysicalCopyBook.
     * This query filters based on the `book_type` discriminator column.
     * @return A list of all physical copy books.
     */
    @Query("SELECT p FROM PhysicalCopyBook p")
    List<PhysicalCopyBook> findAllPhysicalCopyBooks();

    /**
     * Finds all books that are of type EBook.
     * This query filters based on the `book_type` discriminator column.
     * @return A list of all ebook books.
     */
    @Query("SELECT e FROM EBook e")
    List<EBook> findAllEBooks();
}
