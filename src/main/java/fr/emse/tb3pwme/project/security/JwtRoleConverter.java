package fr.emse.tb3pwme.project.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object realmAccessObj = jwt.getClaims().get("realm_access");
        if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
            return Collections.emptyList();
        }
        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles)) {
            return Collections.emptyList();
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Object r : roles) {
            if (r instanceof String) {
                authorities.add(new SimpleGrantedAuthority(((String) r).toUpperCase()));
            }
        }
        return authorities;
    }
}
