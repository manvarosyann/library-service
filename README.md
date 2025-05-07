# Digital Library and Book Rental Service

This is a pure Java implementation of a digital library and book rental system. It supports core library functionalities including book management, rental processing, user account control, and system administration—all backed by in-memory storage and designed using layered architecture.

## Domain Overview

The system is divided into the following subsystems:

- **User Management Subsystem** – Handles user, librarian, and admin accounts.
- **Book Catalog Subsystem** – Manages CRUD operations for book data.
- **Rental Management Subsystem** – Manages the book rental process.

## User Stories

### User

1. As a user, I want to register and log in, so that I can access my personal library account.
2. As a user, I want to browse and search books by title, author, or genre, so that I can find books that interest me.
3. As a user, I want to view detailed information about a book, so that I can decide whether to rent it.
4. As a user, I want to rent a book if it is available, so that I can read it digitally for a limited time.
5. As a user, I want to view my current rented books, so that I can track what I'm reading.
6. As a user, I want to return a rented book, so that I can make it available for others.
7. As a user, I want to see my rental history, so that I can keep track of what I’ve read.
8. As a user, I want to receive return reminders, so that I don't miss rental deadlines.

### Librarian

1. As a librarian, I want to log in with privileged access, so that I can manage the book inventory.
2. As a librarian, I want to add new books, so that users can have more options to choose from.
3. As a librarian, I want to edit book information, so that the catalogue remains accurate.
4. As a librarian, I want to remove outdated or unavailable books, so that users see only valid items.
5. As a librarian, I want to view rental status and user-book associations, so that I can monitor usage.
6. As a librarian, I want to extend book rental periods, so that I can support users who need more time.

### Admin

1. As an admin, I want to manage librarian accounts, so that I can assign and update staff roles.
2. As an admin, I want to manage user accounts, so that I can handle any issues or rule violations.
3. As an admin, I want to disable or delete user accounts, so that I can protect the integrity of the system.
4. As an admin, I want to generate system reports, so that I can monitor platform activity.
5. As an admin, I want to view logs of user and librarian actions, so that I can perform audits when necessary.

## Build & Run

This project uses **Maven Wrapper** and can be built and run via:

```bash
./mvnw clean compile exec:java
