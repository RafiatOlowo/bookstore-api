package com.rafiatolowo.bookstore_api.book;

import com.fasterxml.jackson.annotation.JsonGetter;

import jakarta.persistence.*;

/**
 * Represents a book entity in the bookstore.
 * This class serves as the base for various types of books
 * such as Paperback and Hardcover, using a single-table inheritance strategy.
 */ 
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "book_type", discriminatorType = DiscriminatorType.STRING)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;
    private String title;
    private String author;
    private Integer stock;

    /**
     * Default constructor required by JPA.
     */
    public Book() {
    }

    /**
     * Constructs a new Book with the given details.
     * @param isbn The unique ISBN of the book.
     * @param title The title of the book.
     * @param author The author of the book.
     * @param stock The number of books in stock.
     */
    public Book(String isbn, String title, String author, Integer stock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.stock = stock;
    }

    // New method to expose the book type for JSON serialization
    /**
     * @return The type of the book (e.g., "paperback", "hardcover").
     * This value is derived from the class name for JSON serialization.
     */
    @JsonGetter("bookType")
    public String getBookType() {
        // This is a simple way to get the discriminator value from the class name.
        return this.getClass().getSimpleName().toLowerCase();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}