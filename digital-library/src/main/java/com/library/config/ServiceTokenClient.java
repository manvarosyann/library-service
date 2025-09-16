package com.library.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
public class ServiceTokenClient {
    private final RestClient http;
    private final String clientId;
    private final String clientSecret;
    private final String authBaseUrl;

    private String cachedToken;
    private Instant cachedExpiry = Instant.EPOCH;

    public ServiceTokenClient(
            @Qualifier("lbRestClientBuilder") RestClient.Builder lb,
            @Value("${auth.base-url:http://auth-service}") String authBaseUrl,
            @Value("${auth.client-id:books-service}") String clientId,
            @Value("${auth.client-secret:CHANGE_ME}") String clientSecret
    ) {
        this.http = lb.baseUrl(authBaseUrl).build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authBaseUrl = authBaseUrl;
    }

    public String getToken() {
        if (cachedToken == null || Instant.now().isAfter(cachedExpiry.minusSeconds(30))) {
            var resp = http.post()
                    .uri("/auth/service-token")
                    .body(Map.of(
                            "client_id", clientId,
                            "client_secret", clientSecret
                    ))
                    .retrieve()
                    .body(TokenResponse.class);

            if (resp == null || resp.access_token == null) {
                throw new IllegalStateException("Failed to obtain service token from " + authBaseUrl);
            }
            cachedToken = resp.access_token;
            cachedExpiry = Instant.now().plusSeconds(resp.expires_in != null ? resp.expires_in : 300);
        }
        return cachedToken;
    }

    public static final class TokenResponse {
        public String access_token;
        public String token_type;
        public Long expires_in;
    }
}
