package com.nexsol.tpa.support.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 폼 로그인 비활성화
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Basic 인증 비활성화
                .authorizeExchange(exchanges -> exchanges
                        // 1. 회원가입/로그인 등 Auth 서비스로 가는 요청 허용
                        .pathMatchers("/v1/admin/auth/**").permitAll()
                        // 2. Actuator 헬스체크 허용
                        .pathMatchers("/v1/admin/actuator/**", "/actuator/**").permitAll()
                        // 3. 그 외 요청은 인증 필요 (나중에 JWT 필터 등이 처리)
                        // 개발 중이라 귀찮으면 .anyExchange().permitAll() 로 잠시 풀어도 됨
                        .anyExchange().authenticated()
                )
                .build();
    }

}
