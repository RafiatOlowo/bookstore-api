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
| GET | /api/books | Retrieve a list of all books. |
| GET | /api/books/{isbn} | Retrieve details of a single book by its ISBN. |
| GET | /api/books/{author} | Retrieve a list of all books written by a specific author.
| POST | /api/books | Create a new book entry in the inventory. |
| PUT | /api/books/{isbn} | Update an existing book's details. |
| DELETE | /api/books/{isbn} | Delete a book from the inventory. |

---

## Usage
This section provides practical examples for common API operations using `curl`, a command-line tool for making requests.



---

## Error Handling 
What happens when things go wrong? The project uses try-catch blocks and conditional checks to handle exceptions and specific error cases. Below is a list of tential error messages and their causes.

## Author
* Name: Rafiat Olowo

---





