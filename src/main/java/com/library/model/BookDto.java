package com.library.model;

import com.library.validation.NoForbiddenWord;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class BookDto {
    private Long bookId;

    @Size(min = 2, message = "Title must be at least 2 characters")
    @NoForbiddenWord(message = "Title contains inappropriate language")
    private String title;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotEmpty(message = "At least one author is required")
    private List<Long> authorIds;

    @Column(name = "pages")
    private Integer pages;

    public BookDto() {
    }

    public BookDto(Long bookId, String title, Long sectionId, List<Long> authorIds, Integer pages) {
        this.bookId = bookId;
        this.title = title;
        this.sectionId = sectionId;
        this.authorIds = authorIds;
        this.pages = pages;
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public List<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Long> authorIds) {
        this.authorIds = authorIds;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }
}
