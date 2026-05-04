package com.arsh.seenit.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.service")
public record ServiceProperties(
        String baseUrl,
        String apiKey
) {}
