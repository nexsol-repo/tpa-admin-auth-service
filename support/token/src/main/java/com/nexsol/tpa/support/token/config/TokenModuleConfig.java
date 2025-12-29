package com.nexsol.tpa.support.token.config;

import com.nexsol.tpa.support.token.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan // 현재 패키지의 Component(Provider) 스캔
@EnableConfigurationProperties(JwtProperties.class)
public class TokenModuleConfig {

}
