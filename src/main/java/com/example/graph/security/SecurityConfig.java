package com.example.graph.security;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
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
@Profile("!permit-all")
@EnableMethodSecurity
@EnableConfigurationProperties(AuthSecurityProperties.class)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthSecurityProperties authSecurityProperties,
                                                   JwtAuthConverter jwtAuthConverter) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/api/**"),
                new AntPathRequestMatcher("/public/**")))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/admin/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll();
                if (authSecurityProperties.isPermitPublic()) {
                    auth.requestMatchers(HttpMethod.GET, "/public/graph").permitAll();
                }
                auth.requestMatchers(HttpMethod.POST, "/public/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/public/**").authenticated()
                    .requestMatchers("/public/**", "/api/**").authenticated()
                    .anyRequest().authenticated();
            })
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

        return http.build();
    }

    @Bean
    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter();
    }

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder(AuthSecurityProperties authSecurityProperties,
                                 OAuth2ResourceServerProperties resourceServerProperties) {
        String issuerUri = resourceServerProperties.getJwt().getIssuerUri();
        String jwkSetUri = resourceServerProperties.getJwt().getJwkSetUri();
        NimbusJwtDecoder decoder = buildDecoder(issuerUri, jwkSetUri);
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
        return decoder;
    }

    private NimbusJwtDecoder buildDecoder(String issuerUri, String jwkSetUri) {
        if (StringUtils.hasText(issuerUri)) {
            return (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
        }
        if (StringUtils.hasText(jwkSetUri)) {
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }
        throw new IllegalStateException("JWT decoder requires issuer-uri or jwk-set-uri.");
    }

    private List<String> normalizeAudience(List<String> audiences) {
        if (audiences == null) {
            return List.of();
        }
        return audiences.stream().filter(StringUtils::hasText).toList();
    }
}
