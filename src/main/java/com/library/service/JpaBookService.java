package com.library.service;

import com.library.client.AuthorClient;
import com.library.client.AuthorDto;
import com.library.client.SectionClient;
import com.library.dto.BookFilterRequest;
import com.library.model.*;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JpaBookService {
    private final RemoteValidationService validation;
    private final SectionClient sectionClient;
    private final AuthorClient authorClient;

    private final JpaBookRepository bookRepository;
    private final JpaSectionRepository sectionRepository;
    private final JpaAuthorsRepository authorsRepository;

    public JpaBookService(JpaBookRepository bookRepository, JpaSectionRepository sectionRepository,
                          JpaAuthorsRepository authorsRepository,
                          RemoteValidationService validation, SectionClient sectionClient, AuthorClient authorClient) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.authorsRepository = authorsRepository;
        this.validation = validation;
        this.sectionClient = sectionClient;
        this.authorClient = authorClient;
    }

    @Transactional
    public BookResponseDto createBook(BookCreateDto bookCreateDto) {
        validation.assertSectionExists(bookCreateDto.getSectionId());
        validation.assertAuthorsExist(bookCreateDto.getAuthorIds());

        SectionEntity sectionEntity = sectionRepository.findById(bookCreateDto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = authorsRepository.findAllById(bookCreateDto.getAuthorIds().stream().collect(Collectors.toSet()));
        BookEntity bookEntity = BookMapper.toEntity(bookCreateDto, sectionEntity, authors);
        BookEntity saved = bookRepository.save(bookEntity);

        var section = sectionClient.getById(saved.getSection().getSectionId());
        var authorIds = saved.getAuthors().stream().map(AuthorEntity::getAuthorId).toList();
        var authorDtos = authorIds.isEmpty() ? List.<AuthorDto>of() : authorClient.getByIds(authorIds);
        var authorNames = authorDtos.stream().map(AuthorDto::fullName).toList();
        return new BookResponseDto(saved.getTitle(), section.name(), authorNames);
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        BookEntity e = bookRepository.findById(id).orElseThrow();

        var section = sectionClient.getById(e.getSection().getSectionId());
        var authorIds = e.getAuthors().stream().map(AuthorEntity::getAuthorId).toList();
        var authorDtos = authorIds.isEmpty() ? List.<AuthorDto>of() : authorClient.getByIds(authorIds);
        var authorNames = authorDtos.stream().map(AuthorDto::fullName).toList();

        return new BookResponseDto(e.getTitle(), section.name(), authorNames);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found: " + id);
        }

        bookRepository.deleteById(id);
    }

    @Transactional
    public BookResponseDto updateBook(Long id, BookCreateDto dto) {
        validation.assertSectionExists(dto.getSectionId());
        validation.assertAuthorsExist(dto.getAuthorIds());

        BookEntity existing = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        SectionEntity section = sectionRepository.findById(dto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = new ArrayList<>(authorsRepository.findAllById(dto.getAuthorIds()));

        existing.setTitle(dto.getTitle());
        existing.setSection(section);
        existing.getAuthors().clear();
        existing.getAuthors().addAll(authors);

        BookEntity saved = bookRepository.save(existing);
        var sectionDto = sectionClient.getById(saved.getSection().getSectionId());
        var authorIds = saved.getAuthors().stream().map(AuthorEntity::getAuthorId).toList();
        var authorDtos = authorIds.isEmpty() ? List.<AuthorDto>of() : authorClient.getByIds(authorIds);
        var authorNames = authorDtos.stream().map(AuthorDto::fullName).toList();

        return new BookResponseDto(saved.getTitle(), sectionDto.name(), authorNames);
    }

    @Transactional(readOnly = true)
    public BookEntity getBookByTitle(String title) {
        return bookRepository.findByTitle(title).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<BookEntity> getBooksByGenre(String genre) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional(readOnly = true)
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

        return page.map(e -> {
            var section = sectionClient.getById(e.getSection().getSectionId());
            var authorIds = e.getAuthors().stream().map(AuthorEntity::getAuthorId).toList();
            var authorDtos = authorIds.isEmpty() ? List.<AuthorDto>of() : authorClient.getByIds(authorIds);
            var authorNames = authorDtos.stream().map(AuthorDto::fullName).toList();
            return new BookResponseDto(e.getTitle(), section.name(), authorNames);
        });
    }
}