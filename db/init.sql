drop table if exists
    permissions, membership, borrowing, book_author, author, book,
    admin_details, librarian_details, section, member_details, person
    cascade;

drop type if exists permission_action_enum, membership_type_enum, role_enum;

create type role_enum as enum ('Admin', 'Librarian', 'Member');
create type membership_type_enum as enum ('Regular', 'Premium', 'Student');
create type permission_action_enum as enum ('Add', 'Delete', 'Update', 'View');

-- Creating Tables
create table person
(
    person_id    int generated always as identity primary key,
    first_name   varchar(100)        not null,
    last_name    varchar(100)        not null,
    email        varchar(255) unique not null,
    phone_number varchar(20),
    role         role_enum           not null,
    date_joined  timestamp default current_timestamp
);

create table member_details
(
    user_id   int generated always as identity primary key,
    person_id int unique not null,
    foreign key (person_id) references person (person_id) on delete cascade
);

create table section
(
    section_id           int generated always as identity primary key,
    name                 varchar(100) not null,
    managed_by_person_id int,
    foreign key (managed_by_person_id) references person (person_id) on delete set null
);

create table librarian_details
(
    librarian_id int generated always as identity primary key,
    person_id    int unique not null,
    section_id   int,
    foreign key (person_id) references person (person_id) on delete cascade,
    foreign key (section_id) references section (section_id) on delete set null
);

create table admin_details
(
    admin_id   int generated always as identity primary key,
    person_id  int unique not null,
    privileges text,
    foreign key (person_id) references person (person_id) on delete cascade
);

create table book
(
    book_id    int generated always as identity primary key,
    title      varchar(255) not null,
    section_id int,
    foreign key (section_id) references section (section_id) on delete set null
);

create table author
(
    author_id   int generated always as identity primary key,
    first_name  varchar(100) not null,
    last_name   varchar(100) not null,
    nationality varchar(100),
    birth_date  date,
    biography   text
);

create table book_author
(
    book_id   int,
    author_id int,
    primary key (book_id, author_id),
    foreign key (book_id) references book (book_id) on delete cascade,
    foreign key (author_id) references author (author_id) on delete cascade
);

create table borrowing
(
    borrow_id   int generated always as identity primary key,
    user_id     int not null,
    book_id     int not null,
    borrow_date timestamp default current_timestamp,
    return_date date,
    foreign key (user_id) references member_details (user_id) on delete cascade,
    foreign key (book_id) references book (book_id) on delete cascade
);

create table membership
(
    membership_id   int generated always as identity primary key,
    user_id         int                  not null,
    membership_type membership_type_enum not null,
    expiration_date date                 not null,
    foreign key (user_id) references member_details (user_id) on delete cascade
);

create table permissions
(
    person_id int                    not null,
    action    permission_action_enum not null,
    allowed   boolean                not null,
    primary key (person_id, action),
    foreign key (person_id) references person (person_id) on delete cascade
);


-- Inserting
insert into person (first_name, last_name, email, phone_number, role)
values ('Alice', 'Smith', 'alice.smith@example.com', '091234567', 'Member'),
       ('Bob', 'Brown', 'bob.brown@example.com', '098765432', 'Librarian'),
       ('Carol', 'Jones', 'carol.jones@example.com', '095556677', 'Admin');

insert into member_details (person_id)
values (1);

insert into section (name, managed_by_person_id)
values ('Fiction', 2),
       ('Science', null);

insert into librarian_details (person_id, section_id)
values (2, 1);

insert into admin_details (person_id, privileges)
values (3, 'Full Access');

insert into author (first_name, last_name, nationality, birth_date, biography)
values ('George', 'Orwell', 'British', '1903-06-25', 'Author of 1984 and Animal Farm'),
       ('Jane', 'Austen', 'British', '1775-12-16', 'Renowned novelist'),
       ('Isaac', 'Asimov', 'American', '1920-01-02', 'Science fiction and popular science');

insert into book (title, section_id)
values ('1984', 1),
       ('Pride and Prejudice', 1),
       ('Foundation', 2);

insert into book_author (book_id, author_id)
values (1, 1),
       (2, 2),
       (3, 3);

insert into borrowing (user_id, book_id, borrow_date, return_date)
values (1, 1, '2025-01-10 10:00:00', '2025-01-20'),
       (1, 2, '2025-02-01 14:00:00', null);

insert into membership (user_id, membership_type, expiration_date)
values (1, 'Premium', '2025-12-31');

insert into permissions (person_id, action, allowed)
values (1, 'View', true),
       (2, 'Add', true),
       (2, 'Delete', true),
       (2, 'Update', true),
       (3, 'Add', true),
       (3, 'Delete', true),
       (3, 'Update', true),
       (3, 'View', true);

-- 1. At least one full CRUD set of queries for your domain entity

-- CRUD queries for Book entity
-- Create
insert into book(title, section_id)
values ('Brave New World', 2);

-- Read
select *
from book;

select *
from book
where title = '1984';

select b.book_id, b.title, a.first_name || ' ' || a.last_name as author_name
from book b
         join book_author ba on b.book_id = ba.book_id
         join author a on ba.author_id = a.author_id;

-- Update
update book
set title      = 'Nineteen Eighty-Four',
    section_id = 2
where book_id = 1;

-- Delete
delete
from book
where book_id = 3;

-- 2. Search query with dynamic filters, pagination and sorting
insert into book (title, section_id)
values ('Foundation and Empire', 2);

insert into book (title, section_id)
values ('Second Foundation', 2);

insert into book_author (book_id, author_id)
values (5, 3),
       (6, 3);


-- Find up to 5 science books whose titles contain the word “Foundation,” showing each book’s title, its section name, and its author(s), sorted alphabetically by title and starting from the first result.
select b.book_id, b.title, s.name as section_name, STRING_AGG(a.first_name || ' ' || a.last_name, ', ') as authors
from book b
         left join section s on b.section_id = s.section_id
         left join book_author ba on b.book_id = ba.book_id
         left join author a on ba.author_id = a.author_id
where b.title ilike '%Foundation%' and b.section_id = 2
group by b.book_id, b.title, s.name
order by b.title
    limit 5
offset 0;

-- 3. Search query with joined data for your use-cases
-- Use case: As a user, I want to view my current rented books, so that I can track what I'm reading.

-- Retrieve a list of all books currently rented by a specific user, showing the book title, section name, borrow date, and authors.
select b.book_id,
       b.title,
       s.name                                               as section_name,
       br.borrow_date,
       STRING_AGG(a.first_name || ' ' || a.last_name, ', ') as authors
from borrowing br
         join book b on br.book_id = b.book_id
         left join section s on b.section_id = s.section_id
         left join book_author ba on b.book_id = ba.book_id
         left join author a on ba.author_id = a.author_id
where br.user_id = 1
  and br.return_date is null
group by b.book_id, b.title, s.name, br.borrow_date
order by br.borrow_date desc;

-- 4. Statistic query; can be not related to your use-cases (for example, return authors and number of books they wrote)

-- Return each author's full name and the number of books they have written, ordered from most to least.
select a.first_name || ' ' || a.last_name as author_name, count(ba.book_id) as book_count
from author a
         left join book_author ba on a.author_id = ba.author_id
group by a.author_id, a.first_name, a.last_name
order by book_count desc;

-- 5. Top-something query (for example, return authors and number of books they wrote ordered by books count)

-- Return the top most borrowed book along with its borrow count
select b.title, count(br.borrow_id) as borrow_count
from book b
         join borrowing br on b.book_id = br.book_id
group by b.book_id, b.title
order by borrow_count desc limit 1;
