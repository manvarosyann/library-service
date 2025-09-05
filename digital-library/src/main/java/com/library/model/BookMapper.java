package com.library.model;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    public static BookCreateDto toDto(BookEntity bookEntity) {
        BookCreateDto dto = new BookCreateDto();
        dto.setBookId(bookEntity.getBookId());
        dto.setTitle(bookEntity.getTitle());
        dto.setSectionId(bookEntity.getSection().getSectionId());
        dto.setAuthorIds(
                bookEntity.getAuthors()
                        .stream()
                        .map(AuthorEntity::getAuthorId).collect(Collectors.toList()));
        return dto;
    }

    public static BookEntity toEntity(BookCreateDto dto, SectionEntity section, List<AuthorEntity> authors) {
        BookEntity entity = new BookEntity();
        entity.setTitle(dto.getTitle());
        entity.setSection(section);
        entity.setAuthors(authors);
        return entity;
    }

    public static void updateEntity(BookEntity bookEntity, BookUpdateDto dto, SectionEntity section, List<AuthorEntity> authors) {
        if (dto.getTitle() != null) {
            bookEntity.setTitle(dto.getTitle());
        }
        if (section != null) {
            bookEntity.setSection(section);
        }
        if (authors != null && !authors.isEmpty()) {
            bookEntity.setAuthors(authors);
        }
    }

    public static BookResponseDto toResponseDto(BookEntity bookEntity) {
        BookResponseDto dto = new BookResponseDto();
        dto.setTitle(bookEntity.getTitle());
        dto.setSectionName(bookEntity.getSection().getName());
        dto.setAuthorNames(bookEntity.getAuthors()
                .stream()
                .map(AuthorEntity::getFullName)
                .collect(Collectors.toList()));
        return dto;
    }
}