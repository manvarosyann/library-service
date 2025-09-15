package org.example.authservice.dto;

public record LoginRequest(
        @jakarta.validation.constraints.Email String email,
        @jakarta.validation.constraints.NotBlank String password
) {
}
