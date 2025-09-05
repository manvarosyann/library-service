package com.library.service;

import com.library.client.AuthorClient;
import com.library.client.AuthorDto;
import com.library.client.SectionClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RemoteValidationService {
    private final AuthorClient authors;
    private final SectionClient sections;

    public RemoteValidationService(AuthorClient authors, SectionClient sections) {
        this.authors = authors;
        this.sections = sections;
    }

    public void assertSectionExists(Long sectionId) {
        try {
            sections.assertExists(sectionId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Section not found: " + sectionId);
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException("Sections API error: " + e.getStatusCode());
        } catch (RestClientException e) {
            throw new IllegalStateException("Sections API unavailable", e);
        }
    }

    public void assertAuthorsExist(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty())
            throw new IllegalArgumentException("authorIds must not be empty");
        List<AuthorDto> found = authors.getByIds(authorIds);
        Set<Long> foundIds = found.stream().map(AuthorDto::authorId).collect(Collectors.toSet());
        Set<Long> req = new HashSet<>(authorIds);
        req.removeAll(foundIds);
        if (!req.isEmpty()) throw new IllegalArgumentException("Missing authors: " + req);
    }
}

