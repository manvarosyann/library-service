package com.library.repository;

import com.library.model.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAuthorsRepository extends JpaRepository<AuthorEntity, Long> {
}
