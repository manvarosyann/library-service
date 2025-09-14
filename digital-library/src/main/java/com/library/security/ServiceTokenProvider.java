package com.library.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class ServiceTokenProvider {

    public String issueServiceToken() {
        try {
            var now = Instant.now();
            var claims = new JWTClaimsSet.Builder()
                    .subject("books-service")
                    .issuer("books-service")
                    .audience(List.of("authors-service", "sections-service"))
                    .claim("roles", List.of("Service"))
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(300)))
                    .build();

            var signed = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signed.sign(new MACSigner(com.library.config.JwtKeys.hmacKey().getEncoded()));
            return signed.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot issue service token", e);
        }
    }
}
