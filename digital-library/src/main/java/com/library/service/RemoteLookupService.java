package com.library.service;

import com.library.client.AuthorClient;
import com.library.client.AuthorDto;
import com.library.client.SectionClient;
import com.library.model.AuthorEntity;
import com.library.model.SectionEntity;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaSectionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RemoteLookupService {
    private final SectionClient sectionClient;
    private final AuthorClient authorClient;
    private final JpaSectionRepository sectionRepository;
    private final JpaAuthorsRepository authorsRepository;

    public RemoteLookupService(SectionClient sectionClient, AuthorClient authorClient,
                               JpaSectionRepository sectionRepository, JpaAuthorsRepository authorsRepository) {
        this.sectionClient = sectionClient;
        this.authorClient = authorClient;
        this.sectionRepository = sectionRepository;
        this.authorsRepository = authorsRepository;
    }

    @CircuitBreaker(name = "sectionService", fallbackMethod = "fallbackSectionName")
    @Retry(name = "sectionService")
    public String fetchSectionName(Long sectionId) {
        return sectionClient.getById(sectionId).name();
    }

    public String fallbackSectionName(Long sectionId, Throwable t) {
        return sectionRepository.findById(sectionId)
                .map(SectionEntity::getName)
                .orElse("Unknown section");
    }

    @CircuitBreaker(name = "authorService", fallbackMethod = "fallbackAuthorNames")
    @Retry(name = "authorService")
    public List<String> fetchAuthorNames(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) return List.of();
        return authorClient.getByIds(authorIds).stream().map(AuthorDto::fullName).toList();
    }

    public List<String> fallbackAuthorNames(List<Long> authorIds, Throwable t) {
        if (authorIds == null || authorIds.isEmpty()) return List.of();
        return authorsRepository.findAllById(new HashSet<>(authorIds)).stream()
                .map(a -> a.getFullName().trim())
                .toList();
    }

    @CircuitBreaker(name = "authorService", fallbackMethod = "fallbackAuthorNamesById")
    @Retry(name = "authorService")
    public Map<Long, String> fetchAuthorNamesById(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        var dtos = authorClient.getByIds(new ArrayList<>(ids));
        return dtos.stream().collect(Collectors.toMap(
                AuthorDto::authorId,
                AuthorDto::fullName
        ));
    }

    public Map<Long, String> fallbackAuthorNamesById(Collection<Long> ids, Throwable t) {
        if (ids == null || ids.isEmpty()) return Map.of();
        return authorsRepository.findAllById(new HashSet<>(ids)).stream()
                .collect(Collectors.toMap(AuthorEntity::getAuthorId, a -> a.getFullName().trim()));
    }
}
