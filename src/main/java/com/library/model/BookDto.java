package com.library.model;

import java.util.List;
import java.util.Set;

public class BookDto {
    private Long bookId;
    private String title;
    private Long sectionId;
    private List<Long> authorIds;

    public BookDto() {
    }

    public BookDto(Long bookId, String title, Long sectionId, List<Long> authorIds) {
        this.bookId = bookId;
        this.title = title;
        this.sectionId = sectionId;
        this.authorIds = authorIds;
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
}
