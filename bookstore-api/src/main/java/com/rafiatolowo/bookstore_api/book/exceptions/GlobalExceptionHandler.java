package com.rafiatolowo.bookstore_api.book.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the bookstore API.
 * This class catches specific exceptions thrown by controllers and services
 * and returns a structured and meaningful error response to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
    * Handles IllegalArgumentException.
    * This method is triggered when an {@link java.lang.IllegalArgumentException} is thrown by a controller
    * and maps it to an HTTP 400 Bad Request response. This indicates that the client has sent
    * an invalid request (e.g., incorrect data in the request body or path variables).
    *
    * @param ex The {@link java.lang.IllegalArgumentException} that was thrown.
    * @return A {@link org.springframework.http.ResponseEntity} with a descriptive error message and {@link org.springframework.http.HttpStatus#BAD_REQUEST}.
    */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalStateException.
     * This method is triggered whenever an IllegalStateException is thrown from a controller.
     * It returns an HTTP 409 Conflict status.
     * * @param ex The IllegalStateException that was thrown.
     * @return A ResponseEntity with HttpStatus.CONFLICT.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        // You can return a custom error message to the client here
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handles BookNotFoundException and returns a 404 Not Found status.
     * This is used when a requested book cannot be found in the database.
     * @param ex The BookNotFoundException that was thrown.
     * @return A ResponseEntity with a 404 status and the exception message.
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles BookAlreadyExistsException and returns a 409 Conflict status.
     * This is used when a user attempts to create a book that already exists.
     * @param ex The BookAlreadyExistsException that was thrown.
     * @return A ResponseEntity with a 409 status and the exception message.
     */
    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<String> handleBookAlreadyExistsException(BookAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}