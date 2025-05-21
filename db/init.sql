DROP TABLE IF EXISTS bookauthor, borrowing, membership, book, author, permissions, memberdetails, librariandetails, admindetails, section, person CASCADE;
DROP TYPE IF EXISTS permission_action_enum, membership_type_enum, role_enum;

CREATE TYPE role_enum AS ENUM ('Admin', 'Librarian', 'Member');
CREATE TYPE membership_type_enum AS ENUM ('Regular', 'Premium', 'Student');
CREATE TYPE permission_action_enum AS ENUM ('Add', 'Delete', 'Update', 'View');

CREATE TABLE Person
(
    PersonID    INT PRIMARY KEY,
    FirstName   VARCHAR(100),
    LastName    VARCHAR(100),
    Email       VARCHAR(255),
    PhoneNumber VARCHAR(20),
    Role        role_enum,
    DateJoined  TIMESTAMP
);

CREATE TABLE MemberDetails
(
    UserID   INT PRIMARY KEY,
    PersonID INT UNIQUE,
    FOREIGN KEY (PersonID) REFERENCES Person (PersonID)
);

CREATE TABLE Section
(
    SectionID         INT PRIMARY KEY,
    Name              VARCHAR(100),
    ManagedByPersonID INT,
    FOREIGN KEY (ManagedByPersonID) REFERENCES Person (PersonID)
);

CREATE TABLE LibrarianDetails
(
    LibrarianID INT PRIMARY KEY,
    PersonID    INT UNIQUE,
    SectionID   INT,
    FOREIGN KEY (PersonID) REFERENCES Person (PersonID),
    FOREIGN KEY (SectionID) REFERENCES Section (SectionID)
);

CREATE TABLE AdminDetails
(
    AdminID    INT PRIMARY KEY,
    PersonID   INT UNIQUE,
    Privileges TEXT,
    FOREIGN KEY (PersonID) REFERENCES Person (PersonID)
);

CREATE TABLE Book
(
    BookID    INT PRIMARY KEY,
    Title     VARCHAR(255),
    SectionID INT,
    FOREIGN KEY (SectionID) REFERENCES Section (SectionID)
);

CREATE TABLE Author
(
    AuthorID    INT PRIMARY KEY,
    FirstName   VARCHAR(100),
    LastName    VARCHAR(100),
    Nationality VARCHAR(100),
    BirthDate   DATE,
    Biography   TEXT
);

CREATE TABLE BookAuthor
(
    BookID   INT,
    AuthorID INT,
    PRIMARY KEY (BookID, AuthorID),
    FOREIGN KEY (BookID) REFERENCES Book (BookID),
    FOREIGN KEY (AuthorID) REFERENCES Author (AuthorID)
);

CREATE TABLE Borrowing
(
    BorrowID   INT PRIMARY KEY,
    UserID     INT,
    BookID     INT,
    BorrowDate TIMESTAMP,
    ReturnDate DATE,
    FOREIGN KEY (UserID) REFERENCES MemberDetails (UserID),
    FOREIGN KEY (BookID) REFERENCES Book (BookID)
);

CREATE TABLE Membership
(
    MembershipID   INT PRIMARY KEY,
    UserID         INT,
    MembershipType membership_type_enum,
    ExpirationDate DATE,
    FOREIGN KEY (UserID) REFERENCES MemberDetails (UserID)
);

CREATE TABLE Permissions
(
    PersonID INT,
    Role     role_enum,
    Action   permission_action_enum,
    Allowed  BOOLEAN,
    FOREIGN KEY (PersonID) REFERENCES Person (PersonID)
);
