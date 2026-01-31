package com.example.graph.auth;

import com.example.graph.auth.dto.AuthStartRequest;
import com.example.graph.auth.dto.AuthVerifyRequest;
import com.example.graph.auth.dto.LogoutRequest;
import com.example.graph.auth.dto.OtpChallengeResponse;
import com.example.graph.auth.dto.RefreshRequest;
import com.example.graph.auth.dto.TokenResponse;
import com.example.graph.auth.dto.AuthVerifyResponse;
import com.example.graph.security.AuthServerProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthServerClient {
    private final WebClient webClient;

    public AuthServerClient(WebClient.Builder webClientBuilder, AuthServerProperties properties) {
        this.webClient = webClientBuilder
            .baseUrl(properties.getBaseUrl())
            .build();
    }

    public OtpChallengeResponse start(AuthStartRequest request) {
        return webClient.post()
            .uri("/api/v1/auth/start")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(OtpChallengeResponse.class)
            .block();
    }

    public AuthVerifyResponse verify(AuthVerifyRequest request) {
        return webClient.post()
            .uri("/api/v1/auth/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(AuthVerifyResponse.class)
            .block();
    }

    public TokenResponse refresh(RefreshRequest request) {
        return webClient.post()
            .uri("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();
    }

    public void logout(LogoutRequest request) {
        webClient.post()
            .uri("/api/v1/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
