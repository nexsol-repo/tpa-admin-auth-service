package com.nexsol.tpa.web.gateway;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScopeCheckGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ScopeCheckGatewayFilterFactory.Config> {

    public ScopeCheckGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String roleHeader = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            String scopeHeader = exchange.getRequest().getHeaders().getFirst("X-User-Scope");

            // MASTER 권한은 무조건 통과
            if ("MASTER".equals(roleHeader)) {
                return chain.filter(exchange);
            }

            // 유저의 Scope 파싱
            Set<String> userScopes = Collections.emptySet();
            if (scopeHeader != null && !scopeHeader.isBlank()) {
                userScopes = Arrays.stream(scopeHeader.split(",")).map(String::trim).collect(Collectors.toSet());
            }

            // 필수 권한(config.requiredScope) 확인
            if (userScopes.contains(config.getRequiredScope())) {
                return chain.filter(exchange);
            }

            // 권한 없음 (차단)
            log.warn("Access Denied. UserRole={}, Required={}, UserScopes={}", roleHeader, config.getRequiredScope(),
                    userScopes);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        };
    }

    @Getter
    @Setter
    public static class Config {

        private String requiredScope; // yml에서 "PUNGSU" 등을 입력받음

    }

}