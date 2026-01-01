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
                .route("pungsu-docs", r -> r.path("/v1/admin/pungsu/docs/**")
                        .filters(f -> f
                                // 1. Docs는 인증/Scope 체크를 하지 않거나 느슨하게 할 수 있음 (필요하다면 scopeCheckFactory 제거)

                                // 2. 경로 재작성: /admin/pungsu/docs/index.html -> /docs/index.html
                                // (Pungsu Service 내부의 WebMvcConfig에서 /docs/** 를 매핑하고 있으므로)
                                .rewritePath("/v1/admin/pungsu/(?<segment>.*)", "/${segment}")

                                // 3. Nginx 라우팅을 위한 Prefix 추가: /docs/index.html -> /upstream/pungsu/docs/index.html
                                .prefixPath("/upstream/pungsu")
                        )
                        .uri(pungsuUrl))

                .route("pungsu-service", r -> r.path("/v1/admin/pungsu/**")
                        .filters(f -> f
                                // .stripPrefix(3)
                                .filter(scopeCheckFactory.apply(c -> c.setRequiredScope("PUNGSU")))
                                .prefixPath("/upstream/pungsu"))
                        .uri(pungsuUrl))

                .route("memo-service", r -> r.path("/v1/admin/memo/**")
                        .filters(f -> f
                                // .stripPrefix(3)
                                .filter(scopeCheckFactory.apply(c -> c.setRequiredScope("MEMO")))
                                .prefixPath("/upstream/memo"))
                        .uri(memoUrl))
                .build();
    }

}