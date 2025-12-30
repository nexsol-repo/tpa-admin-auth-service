package com.nexsol.tpa.support.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (REST API)
            .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable) // Basic 인증 비활성화
            .authorizeHttpRequests(auth -> auth
                // 회원가입, 로그인, 헬스체크는 모두 허용
                .requestMatchers("/v1/admin/auth/**", "/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated());
        return http.build();
    }

}
