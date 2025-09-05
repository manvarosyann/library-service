package com.library.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BookResponseDto {
    private String title;
    private String sectionName;
    private List<String> authorNames;

    public BookResponseDto(String title, String sectionName, List<String> authorNames) {
        this.title = title;
        this.sectionName = sectionName;
        this.authorNames = authorNames;
    }
}
