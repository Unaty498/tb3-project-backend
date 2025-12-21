// java
package fr.emse.tb3pwme.project;

import fr.emse.tb3pwme.project.security.AddCorsHeaderFilter;
import fr.emse.tb3pwme.project.security.DebugJwtRoleConverter;
import fr.emse.tb3pwme.project.security.JwtRoleConverter;
import fr.emse.tb3pwme.project.security.RequestAuthLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("!dev")
@EnableMethodSecurity
@EnableWebSecurity
class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AddCorsHeaderFilter corsFilter) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/locks/**"))
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .authorizeHttpRequests(authz ->
                        authz
                                .requestMatchers("/api/locks/**").permitAll()
                                .requestMatchers("/api/**").authenticated())
                /*.addFilterAfter(new RequestAuthLoggingFilter(), UsernamePasswordAuthenticationFilter.class)*/;

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter());
        return converter;
    }
}
