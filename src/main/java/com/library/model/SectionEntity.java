package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "section")
public class SectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "managed_by_person_id")
    private PersonEntity managedBy;

    public Long getSection_id() {
        return sectionId;
    }

    public void setSection_id(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonEntity getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(PersonEntity managedBy) {
        this.managedBy = managedBy;
    }
}
