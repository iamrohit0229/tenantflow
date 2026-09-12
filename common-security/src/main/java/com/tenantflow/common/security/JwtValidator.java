package com.tenantflow.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.AntPathMatcher;

import javax.crypto.SecretKey;

public class JwtValidator {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtValidator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    /**
     * @throws JwtValidationException if the signature, structure or expiry is not valid
     */
    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtValidationException("Invalid or expired token", ex);
        }
    }

    public String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    public boolean isPublicPath(String path) {
        return jwtProperties.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public String getUserId(Claims claims) {
        return claims.get("userId", String.class);
    }
}
