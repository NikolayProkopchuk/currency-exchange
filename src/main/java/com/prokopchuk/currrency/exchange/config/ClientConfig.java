package com.prokopchuk.currrency.exchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "client.fixer")
public class ClientConfig {
    private String url;
    private String accessKey;
    private String baseCurrencyCode;
    private String requestPath;
}
