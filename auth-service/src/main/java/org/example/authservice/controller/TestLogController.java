package org.example.authservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLogController {
    private static final Logger log = LoggerFactory.getLogger(TestLogController.class);

    @GetMapping("/test-log")
    public String testLog() {
        log.info("Hello from AuthService, ELK integration works!");
        return "ok";
    }
}
