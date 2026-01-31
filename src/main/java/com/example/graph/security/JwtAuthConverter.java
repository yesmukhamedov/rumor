package com.example.graph.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;

public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(extractRoleAuthorities(jwt));
        authorities.addAll(extractScopeAuthorities(jwt));
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private Collection<GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
            .filter(StringUtils::hasText)
            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    private Collection<GrantedAuthority> extractScopeAuthorities(Jwt jwt) {
        Set<String> scopes = new HashSet<>();
        String scopeString = jwt.getClaimAsString("scope");
        if (StringUtils.hasText(scopeString)) {
            scopes.addAll(List.of(scopeString.split(" ")));
        }
        List<String> scp = jwt.getClaimAsStringList("scp");
        if (scp != null) {
            scp.stream().filter(StringUtils::hasText).forEach(scopes::add);
        }
        return scopes.stream()
            .filter(StringUtils::hasText)
            .map(scope -> scope.startsWith("SCOPE_") ? scope : "SCOPE_" + scope)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }
}
