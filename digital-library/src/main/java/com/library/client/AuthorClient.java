package com.library.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/authors")
public interface AuthorClient {
    @GetExchange("/{id}")
    AuthorDto getById(@PathVariable Long id);

    @GetExchange
    List<AuthorDto> getByIds(@RequestParam("ids") List<Long> ids);

    default void assertExists(Long id) {
        getById(id);
    }
}
