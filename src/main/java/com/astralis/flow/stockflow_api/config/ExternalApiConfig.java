package com.astralis.flow.stockflow_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Configuration
public class ExternalApiConfig {

  @Value("${external.api.base-url:https://api.iniciativaaplicativos.com.br/api}")
  private String baseUrl;

  @Value("${external.api.token:DEFAULT_TOKEN}")
  private String token;

  @Value("${external.api.secret:DEFAULT_SECRET}")
  private String secret;

  @Bean
  public WebClient externalApiWebClient() {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("secret", secret)
        .defaultHeader("token", token)
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "application/json")
        .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    return mapper;
  }

  public String getToken() {
    return token;
  }

  public String getSecret() {
    return secret;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}