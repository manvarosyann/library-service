package com.library.service;

import com.library.model.AuthorEntity;
import com.library.model.SectionEntity;
import com.library.dto.BookFilterRequest;
import com.library.model.*;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaBookService {
    private final RemoteLookupService remote;
    private final RemoteValidationService validation;

    private final JpaBookRepository bookRepository;
    private final JpaSectionRepository sectionRepository;
    private final JpaAuthorsRepository authorsRepository;

    public JpaBookService(JpaBookRepository bookRepository,
                          JpaSectionRepository sectionRepository,
                          JpaAuthorsRepository authorsRepository,
                          RemoteValidationService validation,
                          RemoteLookupService remote) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.authorsRepository = authorsRepository;
        this.validation = validation;
        this.remote = remote;
    }

    @Transactional
    public BookResponseDto createBook(BookCreateDto bookCreateDto) {
        validation.assertSectionExists(bookCreateDto.getSectionId());
        validation.assertAuthorsExist(bookCreateDto.getAuthorIds());

        SectionEntity sectionEntity = sectionRepository.findById(bookCreateDto.getSectionId()).orElseThrow();
        List<AuthorEntity> authors = authorsRepository
                .findAllById(bookCreateDto.getAuthorIds().stream().collect(Collectors.toSet()));
        BookEntity saved = bookRepository.save(BookMapper.toEntity(bookCreateDto, sectionEntity, authors));

        String sectionName = remote.fetchSectionName(saved.getSection().getSectionId());
        List<Long> authorIds = saved.getAuthors().stream().map(AuthorEntity::getAuthorId).toList();
        List<String> authorNames = remote.fetchAuthorNames(authorIds);

        return new BookResponseDto(saved.getTitle(), sectionName, authorNames);
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        BookEntity e = bookRepository.findById(id).orElseThrow();
        String sectionName = remote.fetchSectionName(e.getSection().getSectionId());
        List<String> authorNames = remote.fetchAuthorNames(
                e.getAuthors().stream().map(AuthorEntity::getAuthorId).toList()
        );
        return new BookResponseDto(e.getTitle(), sectionName, authorNames);
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

        String sectionName = remote.fetchSectionName(saved.getSection().getSectionId());
        List<Long> authorIds = saved.getAuthors().stream().map(AuthorEntity::getAuthorId).toList();
        List<String> authorNames = remote.fetchAuthorNames(authorIds);

        return new BookResponseDto(saved.getTitle(), sectionName, authorNames);
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

        List<BookEntity> books = page.getContent();

        Map<Long, String> sectionNameById = new HashMap<>();
        books.stream()
                .map(b -> b.getSection().getSectionId())
                .distinct()
                .forEach(secId -> sectionNameById.put(secId, remote.fetchSectionName(secId)));

        Set<Long> allAuthorIds = books.stream()
                .flatMap(b -> b.getAuthors().stream().map(AuthorEntity::getAuthorId))
                .collect(Collectors.toSet());

        Map<Long, String> authorNameById =
                allAuthorIds.isEmpty() ? Map.of() : remote.fetchAuthorNamesById(allAuthorIds);

        return page.map(e -> {
            String sectionName = sectionNameById.get(e.getSection().getSectionId());
            List<String> authorNames = e.getAuthors().stream()
                    .map(a -> authorNameById.getOrDefault(a.getAuthorId(), a.getFullName().trim()))
                    .toList();
            return new BookResponseDto(e.getTitle(), sectionName, authorNames);
        });
    }
}
