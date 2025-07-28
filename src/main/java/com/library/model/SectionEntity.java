package com.library.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "sectionId", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonEntity getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(PersonEntity managedBy) {
        this.managedBy = managedBy;
    }

    public Set<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(Set<BookEntity> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "SectionEntity{" +
                "sectionId=" + sectionId +
                ", name='" + name + '\'' +
                ", managedBy=" + managedBy +
                ", books=" + books +
                '}';
    }
}
