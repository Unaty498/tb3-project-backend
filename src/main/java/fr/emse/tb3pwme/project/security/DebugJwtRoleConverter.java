package fr.emse.tb3pwme.project.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class DebugJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger logger = LoggerFactory.getLogger(DebugJwtRoleConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        logger.debug("JWT claims: {}", jwt.getClaims());

        logger.debug("JWT issuer: {}", jwt.getIssuer());

        Set<String> roles = new HashSet<>();

        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map) {
            Object r = ((Map<?, ?>) realmAccess).get("roles");
            if (r instanceof Collection) {
                ((Collection<?>) r).forEach(v -> roles.add(String.valueOf(v)));
            }
        }

        Object rolesClaim = jwt.getClaims().get("roles");
        if (rolesClaim instanceof Collection) {
            ((Collection<?>) rolesClaim).forEach(v -> roles.add(String.valueOf(v)));
        }

        Object authorities = jwt.getClaims().get("authorities");
        if (authorities instanceof Collection) {
            ((Collection<?>) authorities).forEach(v -> roles.add(String.valueOf(v)));
        }

        // Fallback: scopes
        Object scope = jwt.getClaims().get("scope");
        if (scope instanceof String) {
            Arrays.stream(((String) scope).split(" ")).forEach(roles::add);
        }

        logger.debug("Extracted raw roles: {}", roles);

        Collection<GrantedAuthority> granted = roles.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r.toUpperCase(Locale.ROOT))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        logger.debug("Converted GrantedAuthorities: {}", granted);
        return granted;
    }
}
