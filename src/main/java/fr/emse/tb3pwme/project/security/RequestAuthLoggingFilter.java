package fr.emse.tb3pwme.project.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class RequestAuthLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestAuthLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String authorities = auth.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            logger.debug("Request [{} {}] principal={} authorities={}", request.getMethod(), request.getRequestURI(),
                    auth.getName(), authorities);
        } else {
            logger.debug("Request [{} {}] unauthenticated", request.getMethod(), request.getRequestURI());
        }
    }
}
