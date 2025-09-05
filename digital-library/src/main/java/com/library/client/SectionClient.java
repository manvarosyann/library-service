package com.library.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/sections")
public interface SectionClient {
    @GetExchange("/{id}")
    SectionDto getById(@PathVariable Long id);

    default void assertExists(Long id) {
        getById(id);
    }
}
