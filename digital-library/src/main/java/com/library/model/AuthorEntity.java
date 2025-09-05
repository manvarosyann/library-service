package com.library.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "author")
@Data
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String nationality;

    @Column(name = "birth_date")
    private Date birthDate;

    private String biography;

    @ManyToMany(mappedBy = "authors")
    private List<BookEntity> books = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
