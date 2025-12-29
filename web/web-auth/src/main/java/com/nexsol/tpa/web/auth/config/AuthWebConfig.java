package com.nexsol.tpa.web.auth.config;

import com.nexsol.tpa.web.auth.LoginAdminArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.nexsol.tpa.web.auth")
@RequiredArgsConstructor
public class AuthWebConfig implements WebMvcConfigurer {

    private final LoginAdminArgumentResolver loginAdminArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAdminArgumentResolver);
    }

}
