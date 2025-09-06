package com.rafiatolowo.bookstore_api.book.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to be thrown when a book with the same ISBN already exists.
 * mapped to an HTTP 409 Conflict status code.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String message) {
        super(message);
    }
}
