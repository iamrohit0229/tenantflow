package com.tenantflow.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return jwtValidator.isPublicPath(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtValidator.extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (token != null) {
            try {
                Claims claims = jwtValidator.parse(token);
                // No authorities yet — roles arrive with the RBAC task.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                jwtValidator.getEmail(claims), null, List.of());
                authentication.setDetails(jwtValidator.getUserId(claims));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtValidationException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
