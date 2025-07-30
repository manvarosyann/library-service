package com.library.service;

import com.library.model.*;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import jakarta.persistence.criteria.Path;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

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

    public List<BookDto> getFilteredBooks(String title, Long sectionId, List<Long> authorIds,
                                          String sortBy, String direction,
                                          Integer minPages, Integer maxPages) {
        List<BookEntity> entities = bookRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null) {
                predicates.add(cb.equal(root.get("title"), title));
            }

            if (sectionId != null) {
                predicates.add(cb.equal(root.get("section").get("sectionId"), sectionId));
            }

            if (authorIds != null && !authorIds.isEmpty()) {
                predicates.add(root.join("authors").get("authorId").in(authorIds));
            }

            if (sortBy != null) {
                Path<Object> sortPath = root.get(sortBy);
                query.orderBy("desc".equalsIgnoreCase(direction) ? cb.desc(sortPath) : cb.asc(sortPath));
            }

            if (minPages != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pages"), minPages));
            }

            if (maxPages != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pages"), maxPages));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
        return entities.stream().
                map(BookMapper::toDto).
                collect(Collectors.toList());
    }
}
