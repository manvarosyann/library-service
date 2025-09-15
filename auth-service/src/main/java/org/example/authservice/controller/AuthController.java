package org.example.authservice.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.example.authservice.domain.DemoUser;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.example.authservice.security.JwtKeys;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final Map<String, DemoUser> users;

    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.users = new HashMap<>();
        users.put("admin@lib", new DemoUser(1L, "admin@lib", passwordEncoder.encode("adminpass"), List.of("Admin")));
        users.put("librarian@lib", new DemoUser(2L, "librarian@lib", passwordEncoder.encode("librarianpass"), List.of("Librarian")));
        users.put("member@lib", new DemoUser(3L, "member@lib", passwordEncoder.encode("member"), List.of("Member")));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) throws Exception {
        var u = users.get(req.email());
        if (u == null || !passwordEncoder.matches(req.password(), u.password())) {
            return ResponseEntity.status(401).build();
        }

        var now = Instant.now();
        var exp = now.plusSeconds(3600);

        var claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(u.id()))
                .claim("email", u.email())
                .claim("roles", u.roles())
                .issuer("auth-service")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .build();

        var signed = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signed.sign(new MACSigner(JwtKeys.hmacKey().getEncoded()));

        return ResponseEntity.ok(new TokenResponse(signed.serialize(), "Bearer", 3600));
    }
}

