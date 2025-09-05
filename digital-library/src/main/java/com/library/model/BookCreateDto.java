package com.library.model;

import com.library.validation.NoForbiddenWord;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookCreateDto {
    private Long bookId;

    @Size(min = 2, message = "Title must be at least 2 characters")
    @NoForbiddenWord(message = "Title contains inappropriate language")
    private String title;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotEmpty(message = "At least one author is required")
    private List<Long> authorIds;

    public BookCreateDto() {
    }

    public BookCreateDto(Long bookId, String title, Long sectionId, List<Long> authorIds) {
        this.bookId = bookId;
        this.title = title;
        this.sectionId = sectionId;
        this.authorIds = authorIds;
    }
}