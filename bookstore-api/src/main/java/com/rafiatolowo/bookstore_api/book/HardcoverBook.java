package com.rafiatolowo.bookstore_api.book;

import jakarta.persistence.Entity;

@Entity
public class HardcoverBook extends Book {

    // You can add fields specific to Hardcover books here, if needed.

    public HardcoverBook() {
        super();
    }
}