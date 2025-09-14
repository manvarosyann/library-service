package com.library.auth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    record DemoUser(Long id, String email, String password, List<String> roles) {

    }

    private static final Map<String, DemoUser> USERS = Map.of(
            "admin@lib", new DemoUser(1L, "admin@lib", "admin", List.of("Admin")),
            "librarian@lib", new DemoUser(2L, "librarian@lib", "librarian", List.of("Librarian")),
            "member@lib", new DemoUser(3L, "member@lib", "member", List.of("Member"))
    );

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String email, @RequestParam String password) throws Exception {
        var u = USERS.get(email);
        if (u == null || !u.password.equals(password)) return ResponseEntity.status(401).build();

        var now = Instant.now();
        var claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(u.id))
                .claim("email", u.email)
                .claim("roles", u.roles)
                .issuer("books-service")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(3600)))
                .build();

        var header = new JWSHeader(JWSAlgorithm.HS256);
        var signed = new SignedJWT(header, claims);
        signed.sign(new MACSigner(com.library.config.JwtKeys.hmacKey().getEncoded()));
        return ResponseEntity.ok(Map.of("access_token", signed.serialize(), "token_type", "Bearer", "expires_in", "3600"));
    }

}
