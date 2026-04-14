package com.astralis.flow.stockflow_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OmieApiConfig {

  @Value("${omie.api.base-url:https://app.omie.com.br/api/v1}")
  private String baseUrl;

  @Value("${omie.api.app-key}")
  private String appKey;

  @Value("${omie.api.app-secret}")
  private String appSecret;

  @Bean
  public WebClient omieApiWebClient() {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "application/json")
        .build();
  }

  public String getAppKey() {
    return appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }
}
