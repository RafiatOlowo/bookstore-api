# BookStore API
This is the Bookstore API, a RESTful service designed to manage a digital catalog of books. Built with Spring Boot, it provides a complete set of endpoints to create, retrieve, update, and delete book records, serving as the backend for a robust inventory management system.

## Core Functionality
This API provides a comprehensive set of endpoints for managing books in a bookstore database. Key features include:

* **CRUD Operations:** Full functionality to **Create**, **Read**, **Update**, and **Delete** book records.

* **Search and Retrieval:** Efficient retrieval of books by **ISBN** or by a specific **author**.

* **Input Validation:** All incoming requests for creating, updating, or deleting books are checked to ensure data integrity and improve the API's reliability.

* **Error Handling:** 
This project uses a centralized, global exception handling mechanism to ensure a consistent and user-friendly experience when errors occur. Instead of handling exceptions in every single controller method.

* **Test Suite:** To ensure the reliability and correctness of the API, this project includes a comprehensive test suite. To run the tests, execute the following command:
```bash
./mvnw clean test
```

---

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#bookstore-api">BookStore API</a></li>
    <li><a href="#core-functionality">Core Functionality</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites-and-technologies-used">Prerequisites and Technologies Used</a></li>
      </ul>
      <ul>
        <li><a href="#database-setup">Database Setup</a></li>
      </ul>
      <ul>
        <li><a href="#configure-the-application">Configure the Application</a></li>
      </ul>
      <ul>
        <li><a href="#run-the-application">Run the Application</a></li>
      </ul>
    </li>
    <li><a href="#database-schema">Database Schema</a></li>
    <li><a href="#api-endpoints">API Endpoints</a></li>
    <li><a href="#api-documentation-swagger-ui">API Documentation (Swagger UI)</a></li>
    <li>
      <a href="#api-usage">API Usage</a>
      <ul>
        <li><a href="#adding-a-new-book">1. Adding a New Book</a></li>
      </ul>
      <ul>
        <li><a href="#retrieving-all-books">2. Retrieving All Books</a></li>
      </ul>
      <ul>
        <li><a href="#retrieve-a-single-book-by-isbn">3. Retrieve a Single Book by ISBN</a></li>
      </ul>
      <ul>
        <li><a href="#retrieve-books-by-author">4. Retrieve Books by Author</a></li>
      </ul>
      <ul>
        <li><a href="#update-a-book-by-isbn">5. Update a Book by ISBN</a></li>
      </ul>
      <ul>
        <li><a href="#delete-a-book-by-isbn">6. Delete a Book by ISBN</a></li>
      </ul>
    </li>
    <li><a href="#author">Author</a></li>
  </ol>
</details>

---

## Getting Started
Follow these steps to set up and run the project on your local machine.

### Prerequisites and Technologies Used
* **Java 21**: The programming language for the backend.
* **Spring Boot**: The framework used to build the application.
* **Maven**: The build automation tool.
* **Spring Data JPA**: For data persistence and database interaction.
* **MySQL**: The relational database management system.
* **H2 Database:** An in-memory database used for running tests.
* **SpringDoc OpenAPI / Swagger UI:** For generating interactive API documentation.
* **Git & GitHub**: For version control.

### Database Setup
1. Login to MySQL:

```sh
mysql -u root -p
```
> :bulb: **Note:** If your root user doesn't have a password set, omit the `-p` flag.

2. Create the database running the following SQL commands:

```sh
CREATE DATABASE IF NOT EXISTS bookstore_db;
```

### Initialise Project
1. Go to Spring Initializr

Open your web browser and navigate to this [start.spring.io](https://start.spring.io/).

2. Configure Project: On the Spring Initializr page, configure  project's basic settings.
    * Project: `Maven`.
    * Language: `Java`.
    * Spring Boot: latest stable version.
    * Project Metadata: Fill in project details. 
    * Packaging: `Jar`.
    * Java: `Java 17` or a higher version.

3. Add Dependencies: Add the following dependencies.
    * Spring Web: For building web applications.
    * Spring Data JPA: For database interaction.
    * MySQL Driver: To connect to a MySQL database.
    * Spring Boot DevTools: For development tools.
    * H2 Database: An in-memory database great for testing.

4. Generate and Extract: Extract the zip file and open the project in your IDE. The project will have the necessary folder structure and a pom.xml file with all the dependencies configured. From here, you can start adding your code.

### Configure the Application
1. Create a new file in the same `src/main/resources` directory as `application.properties`. Give it a name that clearly indicates it's for local development and contains sensitive data. This is the file that will hold your actual sensitive credentials.

2. Add the file which contains your database credentials to `.gitignore` file. 

3. Activate the local profile. In the main application.properties file, add `spring.config.import=optional:./<local-file-name>.properties` to tell Spring to load the local configuration. This allows you to override any default properties with your local-specific, sensitive information.

### Run the Application
From the project's root directory, run the application using the Maven wrapper. The application will start on port 8080 by default.

```bash
./mvnw spring-boot:run
```
---

## Database Schema
The database uses a single table `book` to store all book types using a **Single Table Inheritance** strategy, which maps different types of books to a single table.


| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` |`PRIMARY KEY`, `AUTO_INCREMENT`| Unique identifier for each book entry (Primary Key) |
| `isbn` | `VARCHAR(255)` |`NOT NULL`, `UNIQUE`| The International Standard Book Number. This is a critical, unique identifier that must not be empty. This column has a unique constraint. |
| `title` | `VARCHAR(255)` |`NOT NULL`| The title of the book |
| `author` | `VARCHAR(255)` |`NOT NULL`| The author of the book |
| `stock` | `INT` |`NOT NULL`, `DEFAULT 0`| The current number of copies in stock. All entries will have a specified stock count, even if it's zero. |
| `book_type` | `VARCHAR(31)` |`NOT NULL`| The discriminator column that indicates the specific book type (e.g., `ebook` and `physical_copy`) |

A database dump file is included in the project to allow for easy restoration of the application's database schema and data.

**Location**: `backup/bookstore_api_backup.sql`

To restore the database, ensure you have a MySQL server running and use the following command in your terminal:

```bash
mysql -u [your_username] -p [your_database_name] < backup/bookstore_api_backup.sql
```
---

## API Endpoints
The API provides the following endpoints:

| HTTP Method | Endpoint | Description |
| --- | --- | --- |
| POST | /api/books | Create a new book entry in the inventory. |
| GET | /api/books | Retrieve a list of all books. |
| GET | /api/books/{isbn} | Retrieve details of a single book by its ISBN. |
| GET | /api/books/author/{author} | Retrieve a list of all books written by a specific author.
| PATCH | /api/books/{isbn} | Partially update an existing book's details. |
| DELETE | /api/books/{isbn} | Delete a book from the inventory. |

**Base URL:** `http://localhost:8080/api/books`

**Authentication:**
This API does not require authentication for public access.

## API Documentation (Swagger UI)
This API is fully documented using **Swagger UI**, which provides a user-friendly interface to explore and test all available endpoints directly from your browser. This tool helps you understand the request and response structures for each endpoint.

You can access the API documentation at the following URL after the application has started:

`http://localhost:8080/swagger-ui.html`

## API Usage
This section provides practical examples of the API operations using `curl`, a command-line tool for making requests.

<h3 id="adding-a-new-book">1. Adding a New Book</h3>

This endpoint is used to add a new book to the database. It handles both `Ebook` and `PhysicalCopyBook` types based on the `bookType` field in the JSON body.

* **Request:** `POST /api/books`
* **Request Body:** A JSON object containing the book details. The `bookType` field is mandatory for the system to correctly identify the book type.
* **Success Response:** Returns a `201 Created` status code with the newly created book object, including the `id` assigned by the database.
* **Error Response:** 
    * Returns a `409 Conflict status` if a book with the same ISBN already exists.
    * Returns a `400 Bad Request` status if the request body is missing or contains an empty/`null` `isbn`.

* **Example `curl` Command (Ebook addition):**
```bash
curl -X POST http://localhost:8080/api/books \
-H "Content-Type: application/json" \
-d '{
    "isbn": "978-0135957059",
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "stock": 100,
    "bookType": "ebook"
}'
```
* **Example `curl` Command (Physical Copy Book addition):**
```bash
curl -X POST http://localhost:8080/api/books \
-H "Content-Type: application/json" \
-d '{
    "isbn": "978-0134685991",
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "stock": 50,
    "bookType": "physical_copy"
}'
```
* **Example `curl` Command (Empty `isbn` field):**
```bash
curl -X POST \
  http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "",
    "title": "Another Example",
    "author": "Another Test Author",
    "stock": 0,
    "bookType": "physical_copy"
}'
```

* **Example `curl` Command (Missing `isbn` field):**
```bash
curl -X POST \
  http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Example Book",
    "author": "Test Author",
    "stock": 0,
    "bookType": "ebook"
}'
```

<h3 id="retrieving-all-books">2. Retrieving All Books</h3>

This endpoint retrieves a list of all books currently available in the database.

* **Request:** `GET /api/books`
* **Success Response:** Returns a `200 OK` status with a JSON array containing all book objects. The array will be empty if no books have been added yet.

* **Example `curl` command:**
```bash
curl http://localhost:8080/api/books
```

* **Example Response Body:**
```bash
[
  {
    "isbn": "978-0135957059",
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "stock": 100,
    "bookType": "ebook"
  },
  {
    "isbn": "978-0134685991",
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "stock": 50,
    "bookType": "physical_copy"
  }
]
```

<h3 id="retrieve-a-single-book-by-isbn">3. Retrieve a Single Book by ISBN</h3>

This endpoint retrieves a specific book using its unique `isbn`.

* **Request:** `GET /api/books/{isbn}`
* **Path Variable:** Replace `{isbn}` with the actual ISBN of the book you want to retrieve.
* **Success Response:** Returns a `200 OK` status with the book's details.
* **Error Response:** 
    * Returns a `400 Bad Request` status if the provided `isbn` is `null` or empty.
    * Returns a `404 Not Found` status if a book with the specified `isbn` does not exist.

* **Example `curl` Command:**
To get the book with the ISBN `978-0135957059`
```bash
curl -X GET http://localhost:8080/api/books/978-0135957059
```

* **Example Response Body:**

```bash
{
    "isbn": "978-0135957059",
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "stock": 100
}
```

<h3 id="retrieve-books-by-author">4. Retrieve Books by Author</h3>
This endpoint retrieves a list of all books written by a specific author.

* **Request:** `GET	/api/books/author/{author}`
* **Path Variable:** Replace `{author}` with the name of the author you want to retrieve books for.
* **Success Response:** Returns a `200 OK` status with a list of book objects. The list may be empty if no books by the author are found.
* **Error Response:** Returns a `400 Bad Request` status if `author` is `null` or empty.

* **Example `curl` Command:**
To get all books by the author `Andrew Hunt`
```bash
curl -X GET "http://localhost:8080/api/books/author/Andrew%20Hunt"
```

* **Example Response Body:**

```bash
[
    {
        "isbn": "978-0135957059",
        "title": "The Pragmatic Programmer",
        "author": "Andrew Hunt, David Thomas",
        "stock": 100
    },
    {
        "isbn": "978-0201616224",
        "title": "The Mythical Man-Month",
        "author": "Andrew Hunt",
        "stock": 50
    }
]
```

<h3 id="update-a-book-by-isbn">5. Update a Book by ISBN</h3>

This endpoint updates the details of an existing book using its unique ISBN. It supports partial updates, so you only need to include the fields you want to change in the request body. Any omitted fields will remain unchanged.

* **Request:** `PATCH /api/books/{isbn}`
* **Path Variable:** Replace `{isbn}` with the ISBN of the book to update.
* **Request Body:** A JSON object containing the fields to be updated (`title`, `author`, or `stock`).
* **Success Response:** Returns a `200 OK` status with the updated book's details in the response body.
* **Error Response:** 
    * Returns a `400 Bad Request` status if the request body is empty or invalid.
    * Returns a `404 Not Found` status if a book with the specified ISBN does not exist.

* **Example `curl` Command:** To update the stock of the book with the ISBN `978-0135957059`

```bash
curl -X PATCH -H "Content-Type: application/json" -d '{ "stock": 110 }' http://localhost:8080/api/books/978-0135957059
```

* **Example Response Body:**
```bash
{
    "isbn": "978-0135957059",
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "stock": 110

}
```
* **Example `curl` Command: (Empty Request Body)**
```bash
curl -X PATCH 'http://localhost:8080/api/books/978-0061120084'
```
* **Example `curl` Command: (ISBN does not exist)**
```bash
curl -X PATCH -H "Content-Type: application/json" -d '{
    "title": "The Updated Book Title"
}' http://localhost:8080/api/books//9780123456789
```
<h3 id="delete-a-book-by-isbn">6. Delete a Book by ISBN</h3>

This endpoint deletes a book from the inventory using its unique ISBN.

* **Request:** `DELETE /api/books/{isbn}`
* **Path Variable:** Replace `{isbn}` with the actual ISBN of the book to be deleted.
* **Success Response:** Returns a `204 No Content` status with no response body, indicating a successful deletion.
* **Error Response:** Returns a `404 Not Found` status if the book with the specified ISBN does not exist.

* **Example `curl` command:**
To delete the book with the ISBN 978-0135957059

```bash
curl -X DELETE http://localhost:8080/api/books/978-0135957059
```
---

## Author
**Name:** Rafiat Olowo

---