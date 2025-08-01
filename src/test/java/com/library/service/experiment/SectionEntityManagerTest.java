package com.library.service.experiment;

import com.library.model.BookEntity;
import com.library.model.SectionEntity;
import com.library.repository.JpaBookRepository;
import com.library.repository.JpaSectionRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class SectionEntityManagerTest {

    @Autowired
    private JpaSectionRepository sectionRepository;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    void clearDatabase() {
        testEntityManager.getEntityManager().createNativeQuery("delete from book_author").executeUpdate();
        testEntityManager.getEntityManager().createNativeQuery("delete from book").executeUpdate();
        testEntityManager.getEntityManager().createNativeQuery("delete from section").executeUpdate();
        testEntityManager.clear();
    }

    // 3. Save Parent without ID using repository.save(), entityManager.persist(), entityManager.merge().
    @Test
    void testSaveUsingRepositorySave() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Philosophy");

        SectionEntity savedSectionEntity = sectionRepository.save(sectionEntity);

        assertNotNull(savedSectionEntity.getSectionId());
        System.out.println("Saved section via repo: ID" + savedSectionEntity.getSectionId());
    }

    @Test
    void testSaveUsingEntityManagerPersist() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Science");

        testEntityManager.persistAndFlush(sectionEntity);

        assertNotNull(sectionEntity.getSectionId());
        System.out.println("Saved section  via EntityManager persist: ID" + sectionEntity.getSectionId());
    }

    @Test
    void testSaveUsingEntityManagerMerge() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("History");

        SectionEntity merged = testEntityManager.merge(sectionEntity);
        testEntityManager.flush();

        assertNotNull(merged.getSectionId());
        System.out.println("Saved section  via EntityManager merge: ID" + sectionEntity.getSectionId());

        assertNotSame(sectionEntity, merged);
    }

    // 4. Save Parent with an initialized ID using repository.save(), entityManager.persist(), entityManager.merge().
    @Test
    void testSaveUsingRepositorySaveWithId() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Philosophy");

        SectionEntity savedSectionEntity = sectionRepository.save(sectionEntity);

        assertNotNull(savedSectionEntity.getSectionId());
    }

    @Test
    void testSaveUsingEntityManagerPersistWithId() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Science");

        testEntityManager.persistAndFlush(sectionEntity);

        assertNotNull(sectionEntity.getSectionId());
    }

    @Test
    void testSaveUsingEntityManagerMergeWithId() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("History");
        testEntityManager.persistAndFlush(sectionEntity);

        Long generatedId = sectionEntity.getSectionId();
        assertNotNull(generatedId);

        SectionEntity detachedEntity = new SectionEntity();
        detachedEntity.setSectionId(generatedId);
        detachedEntity.setName("Updated History");

        SectionEntity mergedEntity = testEntityManager.merge(detachedEntity);
        testEntityManager.flush();

        assertEquals(generatedId, mergedEntity.getSectionId());
        assertNotSame(detachedEntity, mergedEntity);
    }

    // 5. Insert Parent with some ID to the database.
    // Save another Parent with the same ID using repository.save(), entityManager.persist(), entityManager.merge().
    @Test
    void testInsertParentWithTheSameId() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Some New Section");
        SectionEntity savedEntity = sectionRepository.save(sectionEntity);

        Long id = savedEntity.getSectionId();

        SectionEntity anotherEntityWithSameId = new SectionEntity();
        anotherEntityWithSameId.setSectionId(id);
        anotherEntityWithSameId.setName("Another Section With Same ID");

        SectionEntity result = sectionRepository.save(anotherEntityWithSameId);

        sectionRepository.findAll().forEach(System.out::println);

        assertEquals(id, result.getSectionId());
        assertEquals("Another Section With Same ID", result.getName());
    }

    @Test
    void testInsertParentWithTheSameIdUsingEntityManagerPersist() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Some New Section");

        testEntityManager.persistAndFlush(sectionEntity);

        Long id = sectionEntity.getSectionId();

        SectionEntity anotherEntityWithSameId = new SectionEntity();
        anotherEntityWithSameId.setSectionId(id);
        anotherEntityWithSameId.setName("Another Section With Same ID");

        assertThrows(jakarta.persistence.EntityExistsException.class, () -> {
            testEntityManager.persistAndFlush(anotherEntityWithSameId);
        });
    }

    @Test
    void testInsertParentWithTheSameIdUsingEntityManagerMerge() {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setName("Some New Section");

        testEntityManager.persistAndFlush(sectionEntity);

        Long id = sectionEntity.getSectionId();

        SectionEntity anotherEntityWithSameId = new SectionEntity();
        anotherEntityWithSameId.setSectionId(id);
        anotherEntityWithSameId.setName("Another Section With Same ID");

        SectionEntity mergedEntity = testEntityManager.merge(anotherEntityWithSameId);
        testEntityManager.flush();

        assertEquals(id, mergedEntity.getSectionId());
        assertEquals("Another Section With Same ID", mergedEntity.getName());
    }

    // 6. Save the Parent with Children, which are not present in the database - using the same 3 approaches
    @Test
    void testSaveSectionWithNewBooksUsingRepository() {
        SectionEntity section = new SectionEntity();
        section.setName("New Section");

        BookEntity book1 = new BookEntity();
        book1.setTitle("Book One");
        BookEntity book2 = new BookEntity();
        book2.setTitle("Book Two");

        section.getBooks().add(book1);
        section.getBooks().add(book2);

        SectionEntity savedSection = sectionRepository.save(section);

        assertNotNull(savedSection.getSectionId());
        assertFalse(savedSection.getBooks().isEmpty());
        savedSection.getBooks().forEach(book -> assertNotNull(book.getBookId()));
    }

    @Test
    void testSaveSectionWithNewBooksUsingEntityManagerPersist() {
        SectionEntity section = new SectionEntity();
        section.setName("Another Section");

        BookEntity book1 = new BookEntity();
        book1.setTitle("Book Three");
        BookEntity book2 = new BookEntity();
        book2.setTitle("Book Four");

        section.getBooks().add(book1);
        section.getBooks().add(book2);

        testEntityManager.persist(section);
        testEntityManager.flush();

        assertNotNull(section.getSectionId());
        assertFalse(section.getBooks().isEmpty());
        section.getBooks().forEach(book -> assertNotNull(book.getBookId()));
    }

    @Test
    void testSaveSectionWithNewBooksUsingEntityManagerMerge() {
        SectionEntity section = new SectionEntity();
        section.setName("Merged Section");

        BookEntity book1 = new BookEntity();
        book1.setTitle("Book Five");
        BookEntity book2 = new BookEntity();
        book2.setTitle("Book Six");

        section.getBooks().add(book1);
        section.getBooks().add(book2);

        SectionEntity mergedSection = testEntityManager.merge(section);
        testEntityManager.flush();

        assertNotNull(mergedSection.getSectionId());
        assertFalse(mergedSection.getBooks().isEmpty());
        mergedSection.getBooks().forEach(book -> assertNotNull(book.getBookId()));
    }

    // 7. Save the Parent with Children, which are already present in the database - using the same 3 approaches
    @Test
    void testSaveSectionWithExistingBooksUsingRepository() {
        BookEntity book1 = new BookEntity();
        book1.setTitle("Existing Book 1");
        BookEntity savedBook1 = testEntityManager.persistFlushFind(book1);

        BookEntity book2 = new BookEntity();
        book2.setTitle("Existing Book 2");
        BookEntity savedBook2 = testEntityManager.persistFlushFind(book2);

        SectionEntity section = new SectionEntity();
        section.setName("Section with Existing Books");
        section.getBooks().add(savedBook1);
        section.getBooks().add(savedBook2);

        savedBook1.setSectionId(section);
        savedBook2.setSectionId(section);

        SectionEntity savedSection = sectionRepository.save(section);

        assertNotNull(savedSection.getSectionId());
        assertEquals(2, savedSection.getBooks().size());
        savedSection.getBooks().forEach(book -> assertNotNull(book.getBookId()));
    }

    @Test
    void testSaveSectionWithExistingBooksUsingEntityManagerPersist() {
        BookEntity book1 = new BookEntity();
        book1.setTitle("Existing Book 3");
        BookEntity persistedBook1 = testEntityManager.persistFlushFind(book1);

        BookEntity book2 = new BookEntity();
        book2.setTitle("Existing Book 4");
        BookEntity persistedBook2 = testEntityManager.persistFlushFind(book2);

        SectionEntity section = new SectionEntity();
        section.setName("Section with Existing Books Persist");
        section.getBooks().add(persistedBook1);
        section.getBooks().add(persistedBook2);

        persistedBook1.setSectionId(section);
        persistedBook2.setSectionId(section);

        testEntityManager.persist(section);
        testEntityManager.flush();

        assertNotNull(section.getSectionId());
        assertEquals(2, section.getBooks().size());
        section.getBooks().forEach(book -> assertNotNull(book.getBookId()));
    }

    @Test
    void testSaveSectionWithExistingBooksUsingEntityManagerMerge() {
        BookEntity book1 = new BookEntity();
        book1.setTitle("Existing Book 5");
        BookEntity persistedBook1 = testEntityManager.persistFlushFind(book1);

        BookEntity book2 = new BookEntity();
        book2.setTitle("Existing Book 6");
        BookEntity persistedBook2 = testEntityManager.persistFlushFind(book2);

        SectionEntity section = new SectionEntity();
        section.setName("Section with Existing Books Merge");
        section.getBooks().add(persistedBook1);
        section.getBooks().add(persistedBook2);

        persistedBook1.setSectionId(section);
        persistedBook2.setSectionId(section);

        SectionEntity mergedSection = testEntityManager.merge(section);
        testEntityManager.flush();

        assertNotNull(mergedSection.getSectionId());
        assertEquals(2, mergedSection.getBooks().size());
        mergedSection.getBooks().forEach(book -> assertNotNull(book.getBookId()));
    }

    // 8. Save Child without Parent - using the same 3 approaches
    @Test
    void testSaveChildUsingRepositorySaveWithoutParent() {
        BookEntity book = new BookEntity();
        book.setTitle("Book Without Section");
        book.setSectionId(null);

        BookEntity savedBook = bookRepository.save(book);
        assertNotNull(savedBook.getBookId());
        assertNull(savedBook.getSectionId());
    }

    @Test
    void testSaveChildUsingEntityManagerPersistWithoutParent() {
        BookEntity book = new BookEntity();
        book.setTitle("Book Without Section");
        book.setSectionId(null);

        testEntityManager.persistAndFlush(book);

        assertNotNull(book.getBookId());
        assertNull(book.getSectionId());
    }

    @Test
    void testSaveChildUsingEntityManagerMergeWithoutParent() {
        BookEntity book = new BookEntity();
        book.setTitle("Book Without Section");
        book.setSectionId(null);

        BookEntity merged = testEntityManager.merge(book);
        testEntityManager.flush();

        assertNotNull(merged.getBookId());
        assertNull(merged.getSectionId());
        assertNotSame(book, merged);
    }

    // 9. Save Child with Parent initialized, but not present in the database - using the same 3 approaches
    @Test
    void testSaveChildWithNewParentUsingRepositorySave() {
        SectionEntity newSection = new SectionEntity();
        newSection.setName("New Section");

        BookEntity book = new BookEntity();
        book.setTitle("Book With New Section");
        book.setSectionId(newSection);

        BookEntity savedBook = bookRepository.save(book);

        assertNotNull(savedBook.getBookId());
        assertNotNull(savedBook.getSectionId());
        assertNotNull(savedBook.getSectionId().getSectionId());
    }

    @Test
    void testSaveChildWithNewParentUsingEntityManagerPersist() {
        SectionEntity newSection = new SectionEntity();
        newSection.setName("New Section");

        BookEntity book = new BookEntity();
        book.setTitle("Book With New Section");
        book.setSectionId(newSection);

        testEntityManager.persist(book);
        testEntityManager.flush();

        assertNotNull(book.getBookId());
        assertNotNull(book.getSectionId());
        assertNotNull(book.getSectionId().getSectionId());
    }

    @Test
    void testSaveChildWithNewParentUsingEntityManagerMerge() {
        SectionEntity newSection = new SectionEntity();
        newSection.setName("New Section");

        BookEntity book = new BookEntity();
        book.setTitle("Book With New Section");
        book.setSectionId(newSection);

        BookEntity merged = testEntityManager.merge(book);
        testEntityManager.flush();

        assertNotNull(merged.getBookId());
        assertNotNull(merged.getSectionId());
        assertNotNull(merged.getSectionId().getSectionId());
        assertNotSame(book, merged);
    }

    // 10. Save Child with Parent initialized, present in the database, but detached from EntityManager/Session - using the same 3 approaches
    @Test
    void testSaveChildWithDetachedParentUsingRepositorySave() {
        SectionEntity savedParent = sectionRepository.save(new SectionEntity(null, "Existing Section"));

        SectionEntity detachedParent = sectionRepository.findById(savedParent.getSectionId())
                .orElseThrow(() -> new RuntimeException("Parent section not found"));

        BookEntity child = new BookEntity();
        child.setTitle("New Book");
        child.setSectionId(detachedParent);

        BookEntity savedChild = bookRepository.save(child);
        assertNotNull(savedChild.getBookId());
    }

    @Test
    void testSaveChildWithDetachedParentUsingEntityManagerPersist() {
        SectionEntity savedParent = sectionRepository.save(new SectionEntity(null, "Detached Section"));

        SectionEntity detachedParent = sectionRepository.findById(savedParent.getSectionId())
                .orElseThrow(() -> new RuntimeException("Parent section not found"));
        testEntityManager.clear();

        BookEntity child = new BookEntity();
        child.setTitle("Book With Detached Parent");
        child.setSectionId(detachedParent);

        // This will fail if parent is detached (persist expects new/transient entity)
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persist(child);
            testEntityManager.flush();
        });
    }

    @Test
    void testSaveChildWithDetachedParentUsingEntityManagerMerge() {
        SectionEntity savedParent = sectionRepository.save(new SectionEntity(null, "Detached Section"));

        SectionEntity detachedParent = sectionRepository.findById(savedParent.getSectionId())
                .orElseThrow(() -> new RuntimeException("Parent section not found"));
        testEntityManager.clear();

        BookEntity child = new BookEntity();
        child.setTitle("Book With Detached Parent");
        child.setSectionId(detachedParent);

        BookEntity mergedChild = testEntityManager.merge(child);
        testEntityManager.flush();

        assertNotNull(mergedChild.getBookId());
    }

    // 11. Fetch the Parent with JpaRepository, try changing it and don’t save it explicitly.
    // Flush the session and check whether the changes were propagated to the database
    @Test
    void testModifyParentWithoutExplicitSave() {
        SectionEntity section = new SectionEntity();
        section.setName("Original Section");
        SectionEntity savedSection = sectionRepository.save(section);

        SectionEntity fetchedSection = sectionRepository.findById(savedSection.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));

        fetchedSection.setName("Updated Section");

        testEntityManager.flush();

        SectionEntity updatedSection = sectionRepository.findById(savedSection.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));

        assertEquals("Updated Section", updatedSection.getName());
    }

    // 12. Start the transaction, fetch the Parent with JpaRepository, try changing it and don’t save it explicitly.
    // Flush the session and check whether the changes were propagated to the database
    @Test
    @Transactional
    void testUpdateWithoutExplicitSave() {
        SectionEntity section = new SectionEntity();
        section.setName("Original Name");
        section = sectionRepository.save(section);

        Long id = section.getSectionId();

        SectionEntity fetchedSection = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        fetchedSection.setName("Updated Name");

        testEntityManager.flush();

        SectionEntity updatedSection = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        assertEquals("Updated Name", updatedSection.getName());
    }
}