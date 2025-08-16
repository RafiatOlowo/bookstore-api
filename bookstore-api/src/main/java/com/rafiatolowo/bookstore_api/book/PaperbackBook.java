package com.rafiatolowo.bookstore_api.book;

import jakarta.persistence.Entity;

@Entity
public class PaperbackBook extends Book {

    // You can add fields specific to Paperback books here, if needed.

    public PaperbackBook() {
        super();
    }
}