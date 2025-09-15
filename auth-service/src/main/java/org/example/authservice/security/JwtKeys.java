package org.example.authservice.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


public final class JwtKeys {
    private JwtKeys() {
    }

    public static SecretKey hmacKey() {
        String raw = System.getenv().getOrDefault("BOOKS_JWT_SECRET",
                "change-me-dev-secret-min-32-chars");

        return new SecretKeySpec(raw.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
