package com.nexsol.tpa.support.token.config;

import com.nexsol.tpa.support.token.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.nexsol.tpa.support.token")
@EnableConfigurationProperties(JwtProperties.class)
public class TokenModuleConfig {

}
