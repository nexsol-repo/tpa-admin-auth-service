package com.nexsol.tpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.nexsol.tpa") // 설정값(Properties) 스캔 범위 확장
@ComponentScan(basePackages = "com.nexsol.tpa")
public class CoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApiApplication.class, args);
    }

}
