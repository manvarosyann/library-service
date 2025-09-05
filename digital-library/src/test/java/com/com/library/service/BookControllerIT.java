package com.com.library.service;

import com.library.client.AuthorClient;
import com.library.client.AuthorDto;
import com.library.client.SectionClient;
import com.library.client.SectionDto;
import com.library.model.AuthorEntity;
import com.library.model.BookEntity;
import com.library.model.SectionEntity;
import com.library.repository.JpaAuthorsRepository;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import com.library.service.RemoteValidationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookControllerIT {
    private Long existingBookId;

    @Autowired
    private MockMvc mockMvc;

    private JpaBookRepository bookRepository;

    private JpaSectionRepository sectionRepository;

    private JpaAuthorsRepository authorRepository;

    private SectionEntity savedSection;
    private AuthorEntity savedAuthor;

    @MockBean
    private RemoteValidationService remoteValidationService;
    @MockBean
    private SectionClient sectionsClient;
    @MockBean
    private AuthorClient authorsClient;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        sectionRepository.deleteAll();

        SectionEntity section = new SectionEntity();
        section.setName("Fiction");
        section.setManagedBy(null);
        savedSection = sectionRepository.save(section);

        AuthorEntity author = new AuthorEntity();
        author.setFirstName("George");
        author.setLastName("Orwell");
        author.setNationality("British");
        savedAuthor = authorRepository.save(author);

        BookEntity book = new BookEntity();
        book.setTitle("1984");
        book.setSection(savedSection);
        book.setAuthors(new java.util.ArrayList<>(Arrays.asList(savedAuthor)));
        existingBookId = bookRepository.save(book).getBookId();

        doNothing().when(remoteValidationService).assertSectionExists(anyLong());
        doNothing().when(remoteValidationService).assertAuthorsExist(anyList());

        when(sectionsClient.getById(anyLong()))
                .thenAnswer(inv -> new SectionDto(inv.getArgument(0), "Stubbed Section", null));
        when(authorsClient.getById(anyLong()))
                .thenAnswer(inv -> new AuthorDto(inv.getArgument(0), "Stub Author"));

        BookEntity savedBook = bookRepository.save(book);
        this.existingBookId = savedBook.getBookId();
    }

    @Test
    void testGetAllBooks() throws Exception {
        mockMvc.perform(get("/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testCreateBook() throws Exception {
        String newBookJson = String.format("""
                {
                  "title": "Book Title",
                  "sectionId": %d,
                  "authorIds": [%d]
                }
                """, savedSection.getSectionId(), savedAuthor.getAuthorId());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void testUpdateBook() throws Exception {
        String updatedBookJson = String.format("""
                {
                  "title": "Animal Farm",
                  "sectionId": %d,
                  "authorIds": [%d]
                }
                """, savedSection.getSectionId(), savedAuthor.getAuthorId());

        mockMvc.perform(put("/books/" + existingBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Animal Farm"));
    }

    @Test
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/" + existingBookId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetBookById() throws Exception {
        mockMvc.perform(get("/books/" + existingBookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"));
    }

    @Test
    void testCreateBook_InvalidTitle_ShouldReturnValidationError() throws Exception {
        String invalidJson = String.format("""
                {
                  "title": "badword",
                  "sectionId": %d,
                  "authorIds": [%d]
                }
                """, savedSection.getSectionId(), savedAuthor.getAuthorId());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").value("Title contains inappropriate language"));
    }

    @Test
    void testCreateBook_MissingFields_ShouldReturnMultipleValidationErrors() throws Exception {
        String invalidJson = """
                {
                  "title": "",
                  "sectionId": null,
                  "authorIds": []
                }
                """;

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").value("Title must be at least 2 characters"))
                .andExpect(jsonPath("$.errors.sectionId").value("Section ID is required"))
                .andExpect(jsonPath("$.errors.authorIds").value("At least one author is required"));
    }

    @Test
    void testGetBooksFilteredByExactTitle() throws Exception {
        mockMvc.perform(get("/books")
                        .param("title", "1984"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("1984"));
    }

    @Test
    void testSortByTitleAndDescending() throws Exception {
        mockMvc.perform(get("/books")
                        .param("sortBy", "title")
                        .param("direction", "desc"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBooksPaginated() throws Exception {
        for (int i = 0; i < 8; i++) {
            BookEntity book = new BookEntity();
            book.setTitle("Book " + i);
            book.setSection(savedSection);
            book.setAuthors(new java.util.ArrayList<>(Arrays.asList(savedAuthor)));
            bookRepository.save(book);
        }

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(9))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetBooksSecondPage() throws Exception {
        for (int i = 0; i < 8; i++) {
            BookEntity book = new BookEntity();
            book.setTitle("Book " + i);
            book.setSection(savedSection);
            book.setAuthors(new ArrayList<>(Arrays.asList(savedAuthor)));
            bookRepository.save(book);
        }

        mockMvc.perform(get("/books")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "title,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(4))
                .andExpect(jsonPath("$.totalElements").value(9))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1));
    }
}