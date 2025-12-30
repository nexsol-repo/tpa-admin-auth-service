package com.nexsol.tpa.web.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // 환경변수 없으면 디폴트값 사용 (안전장치)
        String authUrl = System.getenv().getOrDefault("AUTH_SERVICE_URL", "http://tpa-admin-auth:8081");
        String pungsuUrl = System.getenv().getOrDefault("PUNGSU_SERVICE_URL", "http://tpa-sun-api:8080");

        return builder.routes()
                // 1. Auth 서비스 라우팅
                .route("auth-service", r -> r.path("/v1/admin/auth/**")
                        .uri(authUrl))

                // 2. 풍수 서비스 라우팅
                .route("pungsu-service", r -> r.path("/v1/admin/pungsu/**")
                        .uri(pungsuUrl))

                .build();
    }
}