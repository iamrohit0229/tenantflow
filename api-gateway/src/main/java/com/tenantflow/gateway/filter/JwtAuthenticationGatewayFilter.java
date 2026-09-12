package com.tenantflow.gateway.filter;

import com.tenantflow.common.security.JwtValidationException;
import com.tenantflow.common.security.JwtValidator;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String UNAUTHORIZED_BODY =
            "{\"status\":401,\"message\":\"Missing or invalid authentication token\",\"data\":null}";

    private final JwtValidator jwtValidator;

    public JwtAuthenticationGatewayFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (jwtValidator.isPublicPath(request.getPath().value())) {
            return chain.filter(withIdentityHeaders(exchange, null, null));
        }

        String token = jwtValidator.extractBearerToken(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (token == null) {
            return unauthorized(exchange);
        }

        Claims claims;
        try {
            claims = jwtValidator.parse(token);
        } catch (JwtValidationException ex) {
            return unauthorized(exchange);
        }

        return chain.filter(withIdentityHeaders(
                exchange, jwtValidator.getUserId(claims), jwtValidator.getEmail(claims)));
    }

    /**
     * Client-supplied identity headers are always dropped first, otherwise a caller could spoof them.
     */
    private ServerWebExchange withIdentityHeaders(ServerWebExchange exchange, String userId, String email) {
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(USER_EMAIL_HEADER);
                    if (userId != null) {
                        headers.add(USER_ID_HEADER, userId);
                    }
                    if (email != null) {
                        headers.add(USER_EMAIL_HEADER, email);
                    }
                })
                .build();
        return exchange.mutate().request(mutated).build();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory()
                .wrap(UNAUTHORIZED_BODY.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
