package com.nexsol.tpa.web.gateway.config;

import com.nexsol.tpa.web.gateway.ScopeCheckGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
            ScopeCheckGatewayFilterFactory scopeCheckFactory) {
        // 환경변수 없으면 디폴트값 사용 (안전장치)
        String authUrl = System.getenv().getOrDefault("AUTH_SERVICE_URL", "http://auth:8081");
        String pungsuUrl = System.getenv()
            .getOrDefault("PUNGSU_SERVICE_URL", "http://host.docker.internal:80/upstream/pungsu");
        String memoUrl = System.getenv()
            .getOrDefault("MEMO_SERVICE_URL", "http://host.docker.internal:80/upstream/memo");

        return builder.routes()
            .route("auth-service", r -> r.path("/v1/admin/auth/**").uri(authUrl))

            .route("pungsu-service", r -> r.path("/v1/admin/pungsu/**")
                .filters(f -> f
                    // .stripPrefix(3) <-- 이 줄을 삭제하거나 주석 처리하세요.
                    .filter(scopeCheckFactory.apply(c -> c.setRequiredScope("PUNGSU"))))
                .uri(pungsuUrl))

            .route("memo-service", r -> r.path("/v1/admin/memo/**")
                .filters(f -> f
                    // .stripPrefix(3) <-- 이 줄을 삭제하거나 주석 처리하세요.
                    .filter(scopeCheckFactory.apply(c -> c.setRequiredScope("MEMO"))))
                .uri(memoUrl))

            .route("pungsu-docs", r -> r.path("/v1/admin/pungsu/docs/**").uri(pungsuUrl))
            .build();
    }

}