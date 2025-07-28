package com.library.service;

import com.library.model.*;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JpaBookServiceDto {
    private final JpaBookRepository bookRepository;
    private final JpaSectionRepository sectionRepository;
    private final JpaAuthorsRepository authorsRepository;

    public JpaBookServiceDto(JpaBookRepository bookRepository, JpaSectionRepository sectionRepository,
                             JpaAuthorsRepository authorsRepository) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.authorsRepository = authorsRepository;
    }

    public BookDto createBook(BookDto bookDto) {
        SectionEntity sectionEntity = sectionRepository.findById(bookDto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = authorsRepository.findAllById(bookDto.getAuthorIds().stream().collect(Collectors.toSet()));
        BookEntity bookEntity = BookMapper.toEntity(bookDto, sectionEntity, authors);
        return BookMapper.toDto(bookRepository.save(bookEntity));
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElse(null);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public BookDto updateBook(Long id, BookDto dto) {
        SectionEntity section = sectionRepository.findById(dto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = new ArrayList<>(authorsRepository.findAllById(dto.getAuthorIds()));
        BookEntity entity = BookMapper.toEntity(dto, section, authors);
        entity.setBookId(id);
        return BookMapper.toDto(bookRepository.save(entity));
    }
}
