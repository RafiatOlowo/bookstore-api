package com.rafiatolowo.bookstore_api.book;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Represents a EBook entity.
 * This class inherits from the base Book class.
 *
 * The @DiscriminatorValue annotation specifies the value
 * that will be stored in the 'book_type' column for this
 * specific subclass.
 */
@Entity
@DiscriminatorValue("ebook")
public class EBook extends Book {

    /**
     * Default constructor required by JPA.
     */
    public EBook() {
    }

    /**
     * Constructs a new EBook with the given details, inheriting from the base Book class.
     * @param isbn The unique ISBN of the book.
     * @param title The title of the book.
     * @param author The author of the book.
     * @param stock The number of books in stock.
     */
    public EBook(String isbn, String title, String author, Integer stock) {
        super(isbn, title, author, stock);
    }
}
