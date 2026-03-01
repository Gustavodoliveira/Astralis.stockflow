package com.astralis.flow.stockflow_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import jakarta.annotation.PostConstruct;

@Configuration
public class ExternalApiConfig {

  @Value("${external.api.base.url:https://api.iniciativaaplicativos.com.br/api}")
  private String baseUrl;

  @Value("${external.api.token:DEFAULT_TOKEN}")
  private String configToken;

  @Value("${external.api.secret:DEFAULT_SECRET}")
  private String configSecret;

  private String token;
  private String secret;

  @PostConstruct
  public void init() {
    // Tentar carregar das variáveis de ambiente primeiro
    String envToken = System.getenv("EXTERNAL_API_TOKEN");
    String envSecret = System.getenv("EXTERNAL_API_SECRET");

    // Usar env vars se disponíveis, senão usar config, senão usar hardcode
    if (envToken != null && !envToken.trim().isEmpty()) {
      this.token = envToken;
      System.out.println("✅ Token carregado das variáveis de ambiente");
    } else if (!configToken.equals("DEFAULT_TOKEN")) {
      this.token = configToken;
      System.out.println("✅ Token carregado da configuração");
    } else {
      this.token = "usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q";
      System.out.println("⚠️ Token usando fallback hardcoded");
    }

    if (envSecret != null && !envSecret.trim().isEmpty()) {
      this.secret = envSecret;
      System.out.println("✅ Secret carregado das variáveis de ambiente");
    } else if (!configSecret.equals("DEFAULT_SECRET")) {
      this.secret = configSecret;
      System.out.println("✅ Secret carregado da configuração");
    } else {
      this.secret = "lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG";
      System.out.println("⚠️ Secret usando fallback hardcoded");
    }

    System.out.println("🔧 Configuração da API externa inicializada");
    System.out.println("📍 Base URL: " + baseUrl);
    System.out.println("🔑 Token length: " + (token != null ? token.length() : "null"));
    System.out.println("🔐 Secret length: " + (secret != null ? secret.length() : "null"));
  }

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

  // Método para debug - não exposer dados sensíveis em produção
  public void logConfiguration() {
    System.out.println("=== CONFIGURAÇÃO API EXTERNA ===");
    System.out.println("Base URL: " + baseUrl);
    System.out.println(
        "Token configurado: " + (token != null && !token.equals("DEFAULT_TOKEN") ? "✅ SIM" : "❌ NÃO (usando default)"));
    System.out.println("Secret configurado: "
        + (secret != null && !secret.equals("DEFAULT_SECRET") ? "✅ SIM" : "❌ NÃO (usando default)"));
    System.out.println("=====================================");
  }
}