package com.messenger.botgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CoreProperties.class)
public class BotGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotGatewayApplication.class, args);
    }
}
