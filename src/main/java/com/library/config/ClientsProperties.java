package com.library.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "clients")
@Data
public class ClientsProperties {
    private ServiceProps authors;
    private ServiceProps sections;

    @Data
    public static class ServiceProps {
        private String baseUrl;
    }
}
