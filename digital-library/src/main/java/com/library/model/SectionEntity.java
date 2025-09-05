package com.library.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "section")
public class SectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "managed_by_person_id")
    private PersonEntity managedBy;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookEntity> books = new HashSet<>();

    public SectionEntity() {
    }

    public SectionEntity(Long sectionId, String name) {
        this.sectionId = sectionId;
        this.name = name;
    }

    public SectionEntity(Long sectionId, String name, PersonEntity managedBy, Set<BookEntity> books) {
        this.sectionId = sectionId;
        this.name = name;
        this.managedBy = managedBy;
        this.books = books;
    }
}