package com.library.model;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    public static BookDto toDto(BookEntity bookEntity) {
        BookDto dto = new BookDto();
        dto.setBookId(bookEntity.getBookId());
        dto.setTitle(bookEntity.getTitle());
        dto.setSectionId(bookEntity.getSection().getSectionId());
        dto.setAuthorIds(bookEntity.getAuthors().stream().map(AuthorEntity::getAuthorId).collect(Collectors.toList()));
        return dto;
    }

    public static BookEntity toEntity(BookDto dto, SectionEntity section, List<AuthorEntity> authors) {
        BookEntity entity = new BookEntity();
        entity.setBookId(dto.getBookId());
        entity.setTitle(dto.getTitle());
        entity.setSection(section);
        entity.setAuthors(authors);
        return entity;
    }
}
