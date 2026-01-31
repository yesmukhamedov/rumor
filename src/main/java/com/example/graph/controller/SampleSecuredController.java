package com.example.graph.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SampleSecuredController {
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        Jwt jwt = jwtAuth.getToken();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("subject", jwt.getSubject());
        response.put("issuer", jwt.getIssuer());
        response.put("audience", jwt.getAudience());
        response.put("authorities", jwtAuth.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .sorted()
            .collect(Collectors.toList()));
        response.put("claims", Map.of("scope", jwt.getClaimAsString("scope"), "roles", jwt.getClaim("roles")));
        return response;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> admin() {
        return Map.of("status", "ok");
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('SCOPE_orders:read')")
    public Map<String, String> orders() {
        return Map.of("status", "ok");
    }
}
