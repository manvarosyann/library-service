package com.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NoForbiddenWordValidator implements ConstraintValidator<NoForbiddenWord, String> {
    private final List<String> forbiddenWords = List.of("badword", "dummy");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || forbiddenWords.stream().noneMatch(value.toLowerCase()::contains);
    }
}
