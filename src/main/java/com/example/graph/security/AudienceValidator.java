package com.example.graph.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final List<String> expectedAudiences;

    public AudienceValidator(List<String> expectedAudiences) {
        this.expectedAudiences = expectedAudiences == null ? List.of() : List.copyOf(expectedAudiences);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (expectedAudiences.isEmpty()) {
            return OAuth2TokenValidatorResult.success();
        }
        Collection<String> audiences = token.getAudience();
        boolean matches = audiences != null && audiences.stream().anyMatch(expectedAudiences::contains);
        if (matches) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
    }
}
