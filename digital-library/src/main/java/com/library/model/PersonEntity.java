package com.library.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "person")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Column(name = "phone_number")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "date_joined")
    private Date dateJoined;

    public enum Role {Admin, Librarian, Member}
}