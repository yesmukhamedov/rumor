package com.example.graph.security;

import java.time.Duration;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieService {
    public static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    private final AuthServerProperties authServerProperties;

    public TokenCookieService(AuthServerProperties authServerProperties) {
        this.authServerProperties = authServerProperties;
    }

    public void setAccessToken(HttpHeaders headers, String token, Long expiresInSeconds) {
        headers.add(HttpHeaders.SET_COOKIE,
            buildAccessCookie(ACCESS_TOKEN_COOKIE, token, expiresInSeconds).toString());
    }

    public void setRefreshToken(HttpHeaders headers, String token) {
        headers.add(HttpHeaders.SET_COOKIE, buildCookie(REFRESH_TOKEN_COOKIE, token,
            authServerProperties.getRefreshCookieMaxAge()).toString());
    }

    public void clearTokens(HttpHeaders headers) {
        headers.add(HttpHeaders.SET_COOKIE, buildClearedCookie(ACCESS_TOKEN_COOKIE).toString());
        headers.add(HttpHeaders.SET_COOKIE, buildClearedCookie(REFRESH_TOKEN_COOKIE).toString());
    }

    private ResponseCookie buildAccessCookie(String name, String value, Long expiresInSeconds) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(authServerProperties.isCookieSecure())
            .sameSite("Lax")
            .path("/");
        Optional.ofNullable(expiresInSeconds)
            .filter(valueSeconds -> valueSeconds > 0)
            .ifPresent(valueSeconds -> builder.maxAge(Duration.ofSeconds(valueSeconds)));
        return builder.build();
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(authServerProperties.isCookieSecure())
            .sameSite("Lax")
            .path("/")
            .maxAge(maxAge)
            .build();
    }

    private ResponseCookie buildClearedCookie(String name) {
        return buildCookie(name, "", Duration.ZERO);
    }
}
