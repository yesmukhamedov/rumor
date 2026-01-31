package com.example.graph.security;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

@Configuration
@Profile("!permit-all")
@EnableConfigurationProperties({AuthServerProperties.class, JwtSecurityProperties.class})
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CookieBearerTokenFilter cookieBearerTokenFilter,
                                                   JwtDecoder jwtDecoder) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/api/**"),
                    new AntPathRequestMatcher("/public/**")))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/public/graph").permitAll()
                .requestMatchers("/login", "/otp/**", "/auth/refresh",
                    "/swagger-ui/**", "/v3/api-docs/**", "/assets/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/public/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/public/**").authenticated()
                .requestMatchers("/admin/**", "/graph/**").authenticated()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.decoder(jwtDecoder)))
            .logout(logout -> logout.disable())
            .addFilterBefore(cookieBearerTokenFilter,
                org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                    new BearerTokenAuthenticationEntryPoint(),
                    new AntPathRequestMatcher("/api/**"))
                .defaultAuthenticationEntryPointFor(
                    new BearerTokenAuthenticationEntryPoint(),
                    new AntPathRequestMatcher("/public/**"))
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new AntPathRequestMatcher("/**")));

        return http.build();
    }

    @Bean
    public CookieBearerTokenFilter cookieBearerTokenFilter() {
        return new CookieBearerTokenFilter(TokenCookieService.ACCESS_TOKEN_COOKIE);
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtSecurityProperties properties,
                                 OAuth2ResourceServerProperties resourceServerProperties) {
        if (StringUtils.hasText(properties.getIssuerUri())) {
            return JwtDecoders.fromIssuerLocation(properties.getIssuerUri());
        }
        if (StringUtils.hasText(properties.getJwkSetUri())) {
            return NimbusJwtDecoder.withJwkSetUri(properties.getJwkSetUri()).build();
        }
        if (StringUtils.hasText(properties.getHmacSecret())) {
            byte[] secret = properties.getHmacSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret, "HmacSHA256")).build();
        }
        if (StringUtils.hasText(resourceServerProperties.getJwt().getIssuerUri())) {
            return JwtDecoders.fromIssuerLocation(resourceServerProperties.getJwt().getIssuerUri());
        }
        if (StringUtils.hasText(resourceServerProperties.getJwt().getJwkSetUri())) {
            return NimbusJwtDecoder.withJwkSetUri(resourceServerProperties.getJwt().getJwkSetUri()).build();
        }
        throw new IllegalStateException("JWT decoder requires issuer-uri or jwk-set-uri (or a dev hmac-secret).");
    }
}
