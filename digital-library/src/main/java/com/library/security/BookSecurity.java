package com.library.security;

import com.library.model.BookEntity;
import com.library.model.SectionEntity;
import com.library.repository.JpaBookRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("bookSecurity")
public class BookSecurity {
    private final JpaBookRepository books;

    public BookSecurity(JpaBookRepository books) {
        this.books = books;
    }

    public boolean canModifyBook(Long bookId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));
        if (isAdmin) return true;

        var opt = books.findById(bookId);
        if (opt.isEmpty()) return false;

        BookEntity b = opt.get();

        Long callerId = Long.valueOf(auth.getName());
        if (b != null && b.getCreatedByUserId() != null
                && b.getCreatedByUserId().equals(callerId)) {
            return true;
        }

        boolean isLibrarian = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Librarian"));
        if (isLibrarian) {
            SectionEntity section = b.getSection();
            if (section != null && section.getManagedBy() != null
                    && section.getManagedBy().getPersonId() != null
                    && section.getManagedBy().getPersonId().equals(callerId)) {
                return true;
            }
        }
        return false;
    }
}
