package com.example.graph.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public class AuthServerProperties {
    private String baseUrl;
    private boolean cookieSecure;
    private Duration refreshCookieMaxAge = Duration.ofDays(30);

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public Duration getRefreshCookieMaxAge() {
        return refreshCookieMaxAge;
    }

    public void setRefreshCookieMaxAge(Duration refreshCookieMaxAge) {
        this.refreshCookieMaxAge = refreshCookieMaxAge;
    }
}
