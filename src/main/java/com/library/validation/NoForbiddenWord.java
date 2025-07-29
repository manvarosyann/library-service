package com.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = NoForbiddenWordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoForbiddenWord {
    String message() default "Forbidden word detected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
