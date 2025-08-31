# BookStore API

As a bookstore inventory manager, I want to have a reliable system for managing our book catalog so that I can accurately track our stock, easily find books for customers, and keep the inventory up-to-date with new titles and changes.

This is the Bookstore API, a Spring Boot application designed to manage a digital catalog of books. It provides a full set of RESTful endpoints to create, retrieve, update, and delete book records, serving as the backend for a bookstore inventory management system.

---

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#bookstore-api">BookStore API</a></li>
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
    <li><a href="#usage">Usage</a></li>
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
    * Java: Java 17 or a higher version.

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
From the project's root directory, run the application using the Maven wrapper.

```bash
./mvnw spring-boot:run
```
---

## Error Handling 
What happens when things go wrong? The project uses try-catch blocks and conditional checks to handle exceptions and specific error cases. Below is a list of tential error messages and their causes.

---
## Database Schema
The database uses a single table `book` to store all book types.

| Column Name | Data Type | Description |
|---|---|---|
| `id` | `BIGINT` | Unique identifier (Primary Key) |
| `isbn` | `VARCHAR(255)` | Unique identifier for the book |
| `title` | `VARCHAR(255)` | The title of the book |
| `author` | `VARCHAR(255)` | The author of the book |
| `stock` | `INT` | The number of books available in stock |
| `book_type` | `VARCHAR(31)` | The type of book (e.g., PaperbackBook) |

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
| PUT | /api/books/{isbn} | Update an existing book's details. |
| DELETE | /api/books/{isbn} | Delete a book from the inventory. |

---

## Usage
This section provides practical examples for common API operations using `curl`, a command-line tool for making requests.

### 1. Adding a New Book

This endpoint is used to add a new book to the database. It handles both `Ebook` and `PhysicalCopyBook` types based on the `bookType` field in the JSON body.

* **Request:** `POST /api/books`
* **Request Body:** A JSON object containing the book details.
* **Success Response:** Returns a `201 Created` status code with the newly created book object, including the `id` assigned by the database.
* **Error Response:** Returns a 409 Conflict status if a book with the same ISBN already exists.

**Example `curl` Command (Ebook):**
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
**Example `curl` Command (Physical Copy):**
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

### 2.  Retrieving All Books

This endpoint retrieves a list of all books currently available in the database. The list can be empty if no books have been added yet.

* **Request:** `GET /api/books`
* **Success Response:** Returns a `200 OK` status with a JSON array containing all book objects. The array will be empty if no books exist.

**Example 'curl' command:**
```bash
curl http://localhost:8080/api/books
```

**Example Response Body:**
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

### 3. Retrieve a Single Book by ISBN

This example shows how to retrieve a specific book by its unique ISBN.
* **Request:** `GET /books/{isbn}`
* **Path Variable:** Replace `{isbn}` with the actual ISBN of the book you want to retrieve.
* **Success Response:** Returns a 200 OK status with the book's details.
* **Error Response:** If a book with the specified ISBN is not found, the server will return a `404 Not Found` status code response with an empty body.

To get the book with the ISBN `978-0135957059`, you would make a `GET` request to:

```bash
GET /api/books/978-0135957059
```

**Example `curl` Command:**

```bash
curl -X GET http://localhost:8080/api/books/978-0135957059
```

**Example Response Body:**
Success Response (200 OK):
```bash
{
    "isbn": "978-0135957059",
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "stock": 100
}
```

### 4. Retrieve Books by Author
This example shows how to retrieve a list of all books written by a specific author.
* **Request:** `GET	/api/books/author/{author}`
* **Path Variable:** Replace `{author}` with the name of the author you want to retrieve books for.
* **Success Response:** Returns a `200 OK` status with a list of book objects. The list may be empty if no books by the author are found.

To get all books by the author "Andrew Hunt", you would make a GET request to:

```bash
GET /api/books/author/Andrew Hunt
```
**Example curl Command:**
```bash
curl -X GET "http://localhost:8080/api/books/author/Andrew%20Hunt"
```

**Example Response Body:**
Success Response (200 OK):
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
### 5. Update a Book by ISBN
This example shows how to update the details of a book in the database using its unique ISBN.  It supports partial updates, meaning you only need to include the fields you wish to change in the request body. Any fields you omit will remain unchanged.

* **Request:** `PUT /api/books/{isbn}`
* **Path Variable:** Replace `{isbn}` with the ISBN of the book you want to update.
* **Request Body:** A JSON object containing the fields to be updated.
* **Success Response:** Returns a `200 OK` status with the updated book's details.
* **Error Response:** Returns a `404 Not Found` status if a book with the specified ISBN does not exist.

To update the stock of the book with the ISBN `978-0135957059`, you would make a `PUT` request with a JSON body:

```bash
PUT /api/books/978-0135957059
```
**Example `curl` Command:**
```bash
curl -X PUT -H "Content-Type: application/json" -d '{ "stock": 110 }' http://localhost:8080/api/books/978-0135957059
```

**Example Response Body:**
Success Response (200 OK):
```bash
{
    "isbn": "978-0135957059",
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "stock": 110

}
```

### 6. Delete a Book by ISBN
This example shows how to delete a book from the inventory using its unique ISBN.
* **Request:** `DELETE /api/books/{isbn}`
* **Path Variable:** Replace `{isbn}` with the actual ISBN of the book to be deleted.
* **Success Response:** Returns a `204 No Content` status.
* **Error Response:** Returns a `404 Not Found` if the book does not exist.

To delete the book with the ISBN 978-0135957059, you would make a DELETE request to:
```bash
DELETE /api/books/978-0135957059
```

**Example `curl` command:**
```bash
curl -X DELETE http://localhost:8080/api/books/978-0135957059
```
---
---

## Author
**Name:** Rafiat Olowo

---