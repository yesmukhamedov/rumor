package com.example.graph.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(AuthSecurityProperties.class)
public class SecurityConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String ISSUER_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.issuer-uri";
    private static final String JWK_SET_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri";

    private final AuthSecurityProperties authSecurityProperties;

    public SecurityConfig(AuthSecurityProperties authSecurityProperties) {
        this.authSecurityProperties = authSecurityProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthConverter jwtAuthConverter,
                                                   Environment environment,
                                                   ObjectProvider<JwtDecoder> jwtDecoderProvider) throws Exception {
        String issuer = environment.getProperty(ISSUER_URI_PROPERTY);
        String jwkSetUri = environment.getProperty(JWK_SET_URI_PROPERTY);
        boolean jwtEnabled = StringUtils.hasText(issuer) || StringUtils.hasText(jwkSetUri);

        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/api/**"),
                new AntPathRequestMatcher("/public/**")))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/admin/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll();
                if (jwtEnabled) {
                    auth.requestMatchers("/public/**", "/api/**").authenticated()
                        .anyRequest().authenticated();
                } else {
                    auth.anyRequest().permitAll();
                }
            });

        if (jwtEnabled) {
            JwtDecoder providedDecoder = jwtDecoderProvider.getIfAvailable();
            final JwtDecoder decoder = providedDecoder != null
                ? providedDecoder
                : buildDecoder(issuer, jwkSetUri);
            final JwtAuthConverter converter = jwtAuthConverter;
            http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.decoder(decoder)
                .jwtAuthenticationConverter(converter)));
        } else {
            LOG.warn("JWT disabled (issuer-uri/jwk-set-uri not set). Running in OPEN MODE.");
            http.oauth2ResourceServer(oauth -> oauth.disable());
        }

        return http.build();
    }

    @Bean
    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter();
    }

    private JwtDecoder buildDecoder(String issuerUri,
                                    String jwkSetUri) {
        if (StringUtils.hasText(issuerUri)) {
            NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
            configureDecoderValidators(decoder, issuerUri);
            return decoder;
        }
        if (!StringUtils.hasText(jwkSetUri)) {
            throw new IllegalStateException("JWT enabled but no issuer/jwks configured");
        }
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        configureDecoderValidators(decoder, null);
        return decoder;
    }

    private void configureDecoderValidators(NimbusJwtDecoder decoder,
                                            String issuerUri) {
        String expectedIssuer = StringUtils.hasText(authSecurityProperties.getExpectedIssuer())
            ? authSecurityProperties.getExpectedIssuer()
            : issuerUri;
        if (StringUtils.hasText(expectedIssuer)) {
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(expectedIssuer),
                new AudienceValidator(normalizeAudience(authSecurityProperties.getExpectedAudience()))));
        } else {
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                new AudienceValidator(normalizeAudience(authSecurityProperties.getExpectedAudience()))));
        }
    }

    private List<String> normalizeAudience(List<String> audiences) {
        if (audiences == null) {
            return List.of();
        }
        return audiences.stream().filter(StringUtils::hasText).toList();
    }
}
