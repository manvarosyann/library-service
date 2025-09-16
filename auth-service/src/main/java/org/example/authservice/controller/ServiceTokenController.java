package org.example.authservice.controller;

import org.example.authservice.dto.TokenResponse;
import org.example.authservice.security.ServiceTokenIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class ServiceTokenController {
    private final ServiceTokenIssuer issuer;
    @Value("${service-clients.digital-library.client-id}")
    String expectedId;
    @Value("${service-clients.digital-library.client-secret}")
    String expectedSecret;

    public ServiceTokenController(ServiceTokenIssuer issuer) {
        this.issuer = issuer;
    }

    record ServiceReq(String client_id, String client_secret) {
    }

    @PostMapping("/service-token")
    public ResponseEntity<TokenResponse> issue(@RequestBody ServiceReq req) throws Exception {
        if (!expectedId.equals(req.client_id()) || !expectedSecret.equals(req.client_secret())) {
            return ResponseEntity.status(401).build();
        }
        var token = issuer.issueFor(req.client_id(), List.of("digital-library"), 300);
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", 300));
    }
}