package com.library.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    private String title;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "section_id")
    private SectionEntity sectionId;

    @ManyToMany
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<AuthorEntity> authors = new ArrayList<>();

    @Column(name = "pages")
    private Integer pages;

    public BookEntity(Long bookId, String title, SectionEntity sectionId, List<AuthorEntity> authors, Integer pages) {
        this.bookId = bookId;
        this.title = title;
        this.sectionId = sectionId;
        this.authors = authors;
        this.pages = pages;
    }

    public BookEntity() {

    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SectionEntity getSection() {
        return sectionId;
    }

    public void setSection(SectionEntity sectionId) {
        this.sectionId = sectionId;
    }

    public List<AuthorEntity> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorEntity> authors) {
        this.authors = authors;
    }

    public SectionEntity getSectionId() {
        return sectionId;
    }

    public void setSectionId(SectionEntity sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }
}
