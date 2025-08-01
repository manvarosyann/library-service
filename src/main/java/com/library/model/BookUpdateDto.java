package com.library.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BookUpdateDto {
    private String title;
    private Long sectionId;
    private List<Long> authorIds;

    public BookUpdateDto(String title, Long sectionId, List<Long> authorIds) {
        this.title = title;
        this.sectionId = sectionId;
        this.authorIds = authorIds;
    }
}
