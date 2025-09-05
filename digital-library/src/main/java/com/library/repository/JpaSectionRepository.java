package com.library.repository;

import com.library.model.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSectionRepository extends JpaRepository<SectionEntity, Long> {

}
