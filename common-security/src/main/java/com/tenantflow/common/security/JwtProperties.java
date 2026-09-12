package com.tenantflow.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64-encoded signing key. Must decode to at least 256 bits for HS256.
     */
    private String secret;

    private long expirationMs;

    /**
     * Ant-style paths reachable without a Bearer token.
     */
    private List<String> publicPaths = new ArrayList<>();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }
}
