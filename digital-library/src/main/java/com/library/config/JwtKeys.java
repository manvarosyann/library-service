package com.library.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public final class JwtKeys {
    private JwtKeys() {
    }

    public static SecretKey hmacKey() {
        String secret = System.getenv("BOOKS_JWT_SECRET");
        if (secret == null || secret.length() < 32) {
            secret = "dev-secret-at-least-32-chars-long-please!!!!";
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
