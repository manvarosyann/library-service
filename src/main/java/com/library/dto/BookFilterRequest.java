package com.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class BookFilterRequest {
    private String title;
    private Long sectionId;
    private List<Long> authorIds;
    private String sortBy;
    private String direction;

    public BookFilterRequest() {
    }

    public BookFilterRequest(String title, Long sectionId, List<Long> authorIds, String sortBy, String direction) {
        this.title = title;
        this.sectionId = sectionId;
        this.authorIds = authorIds;
        this.sortBy = sortBy;
        this.direction = direction;
    }
}
