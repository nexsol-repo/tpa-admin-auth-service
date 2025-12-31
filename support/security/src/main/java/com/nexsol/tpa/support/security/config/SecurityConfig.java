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
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 끄기
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 끄기
                .httpBasic(AbstractHttpConfigurer::disable) // Http Basic 끄기

                .authorizeHttpRequests(auth -> auth
                        // 1. 로그인, 회원가입 등 인증 없이 접근할 경로
                        .requestMatchers("/v1/admin/auth/**","/admin/pungsu/docs/index.html").permitAll()
                        // 2. 헬스체크 경로
                        .requestMatchers("/actuator/**", "/v1/admin/actuator/**").permitAll()
                        // 3. 나머지는 다 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
