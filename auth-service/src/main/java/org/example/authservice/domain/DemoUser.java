package org.example.authservice.domain;

import java.util.List;

public record DemoUser(Long id, String email, String password, List<String> roles) {
}
