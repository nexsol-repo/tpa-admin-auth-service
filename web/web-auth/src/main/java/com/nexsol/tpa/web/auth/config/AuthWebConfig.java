package com.nexsol.tpa.web.auth.config;

import com.nexsol.tpa.support.token.config.TokenModuleConfig;
import com.nexsol.tpa.web.auth.LoginAdminArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.nexsol.tpa.web.auth")
@Import(TokenModuleConfig.class)
@RequiredArgsConstructor
public class AuthWebConfig implements WebMvcConfigurer {

    private final LoginAdminArgumentResolver loginAdminArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAdminArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://admin.tpa.nexsol.ai"
                ) // 허용할 프론트엔드 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키나 인증 헤더 허용 시 true
                .maxAge(3600); // 프리플라이트(preflight) 요청 캐싱 시간
    }

}
