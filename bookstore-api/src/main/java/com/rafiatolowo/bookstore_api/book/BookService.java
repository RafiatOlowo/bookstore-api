package com.rafiatolowo.bookstore_api.book;

import org.springframework.stereotype.Service;

import com.rafiatolowo.bookstore_api.book.exceptions.BookAlreadyExistsException;
import com.rafiatolowo.bookstore_api.book.exceptions.BookNotFoundException;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.util.List;
import java.util.Optional;

/**
 * The service class for handling all business logic related to Book entities.
 * This class acts as a middle layer between the BookController and the BookRepository.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Constructor for dependency injection. Spring will automatically
     * inject the BookRepository instance.
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieves all books from the database.
     * @return A list of all Book entities.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Finds a book by its ISBN (International Standard Book Number).
     * This method implements a "look-aside" caching strategy: it first checks the local
     * database, and if the book is not found, it fetches the data from the Google Books API.
     *
     * @param isbn The ISBN of the book to find.
     * @return An {@code Optional<Book>} containing the found book, or an empty Optional
     * if the book cannot be found in either the local database or the external API.
     */
    public Optional<Book> findByIsbn(String isbn) {
        // --- STEP 1: Validate the input first (best practice) ---
        // This is a crucial check to ensure the ISBN is not null or an empty string.
        // Throwing an IllegalArgumentException prevents the rest of the method from executing
        // with invalid data and immediately tells the caller something is wrong.
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN must not be null or empty");
        }

        // --- STEP 2: Check the local database first ---
        // This is the primary check. It's much faster to get data from our local
        // database than from an external network service.
        Optional<Book> localBook = bookRepository.findByIsbn(isbn);
        if (localBook.isPresent()) {
            // If the book is found, we return it immediately.
            return localBook;
        }

        // --- STEP 3: If not found locally, fetch from the external API ---
        // Clean the ISBN by removing any non-digit characters (like dashes or spaces) 
        // specifically for the API call.
        String cleanedIsbn = isbn.replaceAll("[^\\dX]", "");

        // Construct the full URL for the Google Books API, including the ISBN as a query parameter.
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + cleanedIsbn;

        // A try-catch block is essential for any network call to handle potential
        // issues like network errors, invalid URLs, or timeouts.
        try {
            // Make the GET request to the Google Books API and expect a JSON response.
            HttpResponse<JsonNode> response = Unirest.get(url).asJson();

            // Check if the API request was successful (HTTP status code 200).
            if (response.isSuccess()) {
                // Get the main JSON object from the response body.
                JsonNode rootNode = response.getBody();
                // Check if the response contains a valid "items" array with at least one result.
                if (rootNode != null && rootNode.getObject().has("items") && rootNode.getObject().getJSONArray("items").length() > 0) {
                    // Extract the first item from the "items" array as a JSONObject.
                    JSONObject firstItem = rootNode.getObject().getJSONArray("items").getJSONObject(0);

                    // Extract the relevant data from the nested JSON structure.
                    // The volumeInfo object contains details like title, author, and page count.
                    JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");
                    // The accessInfo object contains details about the availability of the book as an e-book.
                    JSONObject accessInfo = firstItem.getJSONObject("accessInfo");

                    Book newBook;
                    // --- STEP 4: Infer the book type based on the data from the API ---
                    // Determine if an EBook or a PhysicalCopyBook should created.
                    if (accessInfo.has("epub") || accessInfo.has("pdf")) {
                        // If the API indicates it has epub or pdf info, create an EBook.
                        newBook = new EBook();
                    } else if (volumeInfo.has("pageCount")) {
                        // If there's a page count, infer it's a physical copy.
                        newBook = new PhysicalCopyBook();
                    } else {
                        // If type can't be determined, return an empty Optional.
                        return Optional.empty();
                    }

                    // --- STEP 5: Set common properties and save to local database ---
                    // Populate the new Book object with data from the API.
                    newBook.setIsbn(isbn);
                    newBook.setTitle(volumeInfo.getString("title"));

                    // Set stock to default 0
                    newBook.setStock(0);

                    // Check if the 'authors' array exists before trying to access it
                    // to prevent a JSONException.
                    if (volumeInfo.has("authors")) {
                        // Assume the first author is the main one.
                        newBook.setAuthor(volumeInfo.getJSONArray("authors").getString(0));
                    }

                    // Save the newly created book to local database.
                    // It will now be part of our inventory and can be found on subsequent requests.
                    Book savedBook = bookRepository.save(newBook);
                    System.out.println("Book fetched from API and saved to local inventory.");
                    return Optional.of(savedBook);
                }
            }
        } catch (Exception e) {
            // Catch and log any exceptions that occur during the API call or data parsing.
            System.err.println("Error fetching book details from External API: " + e.getMessage());
        }

        // If any part of the process fails (e.g., API request fails, or no items found),
        // Return an empty Optional.
        return Optional.empty();
    }
    
      /**
     * Adds a new book to the database.
     * <p>
     * This method first validates that the provided {@code book} object and its ISBN
     * are not null or empty. It then checks if a book with the same ISBN
     * already exists in the database. If a duplicate is found, a
     * {@link BookAlreadyExistsException} is thrown. Otherwise, the new book
     * is saved and returned.
     * </p>
     *
     * @param book The {@code Book} object to be added to the database. Must not be null and must contain a valid ISBN.
     * @return The {@code Book} entity as it is saved in the database, including any generated IDs.
     * @throws IllegalArgumentException if the provided {@code book} is null or if its ISBN is null or blank.
     * @throws BookAlreadyExistsException if a book with the same ISBN already exists in the repository.
     */
    public Book addBook(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("Book or its ISBN cannot be null or empty.");
        }
        
        // Business logic: Check for duplicates before saving.
        Optional<Book> existingBook = findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException("A book with ISBN " + book.getIsbn() + " already exists.");
        }
        return bookRepository.save(book);
    }
    
    /**
     * Finds a list of books by a specific author.
     * @param author The name of the author to search for.
     * @return A list of books by the specified author.
     * @throws IllegalArgumentException if the author is null or empty.
     */
    public List<Book> findBooksByAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        return bookRepository.findByAuthor(author);
    }

        /**
     * Updates an existing book. This method handles partial updates by
     * only updating the fields that are not null in the provided {@code updatedBook} object.
     * The update is performed on the book identified by the given ISBN.
     *
     * @param isbn The ISBN of the book to update. Must not be null or empty.
     * @param updatedBook The book object containing the new values for the fields to be updated.
     * Fields with a null value will be ignored.
     * @return The fully updated {@code Book} object as it is saved in the database.
     * @throws IllegalArgumentException if the {@code isbn} is null or empty, or if an
     * attempt is made to update immutable fields such as the book's ID or its type.
     * @throws BookNotFoundException if a book with the specified {@code isbn} is not found in the repository.
     */
    public Book updateBook(String isbn, Book updatedBook) {
       // Validation: Check for invalid input first
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found."));
        
        // Validate that immutable fields are not being updated.
        // The ID is generated, and the book's type is part of its identity,
        // so neither should be changeable.
        if (updatedBook.getId() != null) {
            throw new IllegalArgumentException("Book ID cannot be updated.");
        }

        // Used getClass() to prevent changing the book type.
        // This is because bookType is a JPA discriminator, not a field.
        if (!existingBook.getClass().equals(updatedBook.getClass())) {
            throw new IllegalArgumentException("Book type cannot be updated.");
        }
        // Only update fields that are not null in the request body
        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            existingBook.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getStock() != null) {
            existingBook.setStock(updatedBook.getStock());
        }
        
        // Save the updated book to the database
        return bookRepository.save(existingBook);
    }

    /**
     * Deletes a book from the database.
     * 
     * @param isbn The ISBN of the book to delete.
     * @return true if the book was deleted, false otherwise.
     * @throws IllegalArgumentException if the ISBN is null or empty.
     * @throws BookNotFoundException if the book to delete is not found.
     */
    public boolean deleteBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return true;
        } else {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found.");
        }
    }
}
