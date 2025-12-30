package com.nexsol.tpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {
        "com.nexsol.tpa.core",
        "com.nexsol.tpa.storage",
        "com.nexsol.tpa.support.token"
})
@ConfigurationPropertiesScan(basePackages = {
        "com.nexsol.tpa.core",
        "com.nexsol.tpa.storage"
})
public class CoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApiApplication.class, args);
    }

}
