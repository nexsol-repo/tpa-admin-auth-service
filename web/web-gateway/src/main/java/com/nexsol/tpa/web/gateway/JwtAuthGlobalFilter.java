package com.nexsol.tpa.web.gateway;

import com.nexsol.tpa.support.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (path.startsWith("/v1/admin/auth")) {
            return chain.filter(exchange);
        }

        String token = resolveToken(request);
        if (token == null) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
        }

        try {
            jwtTokenProvider.validateToken(token);

            Long userId = jwtTokenProvider.getUserId(token);
            String role = jwtTokenProvider.getRole(token);
            Set<String> scopes = jwtTokenProvider.getScope(token);

            // 5. 헤더 변조 (뒷단 서비스에 유저 정보 전달)
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", role)
                    .header("X-User-Scope", String.join(",", scopes))
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (IllegalAccessError e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token");
        }

    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String msg) {
        log.error("Gateway Auth Error: {}", msg);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
