package com.library.service;

import com.library.dto.BookFilterRequest;
import com.library.model.*;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import jakarta.persistence.criteria.Path;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JpaBookService {
    private final JpaBookRepository bookRepository;
    private final JpaSectionRepository sectionRepository;
    private final JpaAuthorsRepository authorsRepository;

    public JpaBookService(JpaBookRepository bookRepository, JpaSectionRepository sectionRepository,
                          JpaAuthorsRepository authorsRepository) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.authorsRepository = authorsRepository;
    }

    public BookResponseDto createBook(BookCreateDto bookCreateDto) {
        SectionEntity sectionEntity = sectionRepository.findById(bookCreateDto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = authorsRepository.findAllById(bookCreateDto.getAuthorIds().stream().collect(Collectors.toSet()));
        BookEntity bookEntity = BookMapper.toEntity(bookCreateDto, sectionEntity, authors);
        BookEntity savedBookEntity = bookRepository.save(bookEntity);
        return BookMapper.toResponseDto(savedBookEntity);
    }

    public BookResponseDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toResponseDto)
                .orElse(null);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public BookResponseDto updateBook(Long id, BookCreateDto dto) {
        SectionEntity section = sectionRepository.findById(dto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = new ArrayList<>(authorsRepository.findAllById(dto.getAuthorIds()));
        BookEntity entity = BookMapper.toEntity(dto, section, authors);
        entity.setBookId(id);
        BookEntity updatedBookEntity = bookRepository.save(entity);
        return BookMapper.toResponseDto(updatedBookEntity);
    }

    public BookEntity getBookByTitle(String title) {
        return bookRepository.findByTitle(title).orElse(null);
    }

    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<BookEntity> getBooksByGenre(String genre) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Page<BookResponseDto> getFilteredBooks(BookFilterRequest filter, Pageable pageable) {
        Set<String> allowedSortFields = Set.of("title", "bookId");

        if (filter.getSortBy() != null && !allowedSortFields.contains(filter.getSortBy())) {
            throw new IllegalStateException("Invalid sort field: " + filter.getSortBy());
        }

        Page<BookEntity> page = bookRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null) {
                predicates.add(cb.equal(root.get("title"), filter.getTitle()));
            }

            if (filter.getSectionId() != null) {
                predicates.add(cb.equal(root.get("section").get("sectionId"), filter.getSectionId()));
            }

            if (filter.getAuthorIds() != null && !filter.getAuthorIds().isEmpty()) {
                predicates.add(root.join("authors").get("authorId").in(filter.getAuthorIds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        return page.map(BookMapper::toResponseDto);
    }
}