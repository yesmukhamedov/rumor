package com.example.graph.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "auth.expected-issuer=http://issuer.test",
    "auth.expected-audience=rumor"
})
class SecurityIntegrationTest {
    private static final String ISSUER = "http://issuer.test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void apiMeRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void apiMeAcceptsValidToken() throws Exception {
        mockMvc.perform(get("/api/me")
                .header("Authorization", "Bearer " + tokenFor(List.of("USER"), "profile")))
            .andExpect(status().isOk());
    }

    @Test
    void apiAdminRejectsWithoutRole() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + tokenFor(List.of("USER"), "profile")))
            .andExpect(status().isForbidden());
    }

    @Test
    void apiAdminAllowsAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + tokenFor(List.of("ADMIN"), "profile")))
            .andExpect(status().isOk());
    }

    @Test
    void apiOrdersRejectsWithoutScope() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + tokenFor(List.of("USER"), "profile")))
            .andExpect(status().isForbidden());
    }

    @Test
    void apiOrdersAllowsScope() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + tokenFor(List.of("USER"), "orders:read")))
            .andExpect(status().isOk());
    }

    private String tokenFor(List<String> roles, String scope) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(ISSUER)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(300))
            .subject("user-" + UUID.randomUUID())
            .audience(List.of("rumor"))
            .claim("roles", roles)
            .claim("scope", scope)
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @TestConfiguration
    static class JwtTestConfig {
        @Bean
        KeyPair keyPair() throws Exception {
            var keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }

        @Bean
        JwtEncoder jwtEncoder(KeyPair keyPair) {
            RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID("test-key")
                .build();
            return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
        }

        @Bean
        JwtDecoder jwtDecoder(AuthSecurityProperties properties, KeyPair keyPair) {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(properties.getExpectedIssuer()),
                new AudienceValidator(properties.getExpectedAudience())));
            return decoder;
        }
    }
}
