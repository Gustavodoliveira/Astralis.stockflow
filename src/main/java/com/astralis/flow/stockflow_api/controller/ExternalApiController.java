package com.astralis.flow.stockflow_api.controller;

import com.astralis.flow.stockflow_api.config.ExternalApiConfig;
import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemResponse;
import com.astralis.flow.stockflow_api.service.ExampleIntegrationService;
import com.astralis.flow.stockflow_api.service.ExternalApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.examples.Example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/external")
public class ExternalApiController {

  private final ExternalApiService externalApiService;
  private final ExampleIntegrationService exampleIntegrationService;
  private final ExternalApiConfig externalApiConfig;

  @Autowired
  public ExternalApiController(ExternalApiService externalApiService,
      ExampleIntegrationService exampleIntegrationService,
      ExternalApiConfig externalApiConfig) {
    this.externalApiService = externalApiService;
    this.exampleIntegrationService = exampleIntegrationService;
    this.externalApiConfig = externalApiConfig;
  }

  @GetMapping("/getLotByLocation/{location}")
  @Operation(summary = "Buscar lotes por localização")
  public ResponseEntity<String> getLotByLocation(@PathVariable String location) {
    try {
      String items = exampleIntegrationService.getExternalItemsByLocation(location);
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/getLotById/{Id}")
  @Operation(summary = "Buscar lotes por ID do produto")
  public ResponseEntity<String> getLotById(@PathVariable String Id) {
    try {
      String items = exampleIntegrationService.getExternalItemsLotById(Id);
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/getItemByDescription/{description}")
  @Operation(summary = "Buscar itens por descrição")
  public ResponseEntity<String> getItemByDescription(@PathVariable String description) {
    try {
      String items = exampleIntegrationService.getProductsByDescription(description);
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/debug/config")
  @Operation(summary = "Debug: Verificar configuração da API externa")
  public ResponseEntity<Map<String, Object>> debugConfiguration() {
    Map<String, Object> config = new HashMap<>();
    config.put("baseUrl", externalApiConfig.getBaseUrl());
    config.put("tokenConfigured", !externalApiConfig.getToken().equals("DEFAULT_TOKEN"));
    config.put("secretConfigured", !externalApiConfig.getSecret().equals("DEFAULT_SECRET"));
    config.put("tokenLength", externalApiConfig.getToken().length());
    config.put("secretLength", externalApiConfig.getSecret().length());

    // Log no console também
    externalApiConfig.logConfiguration();

    return ResponseEntity.ok(config);
  }

  @GetMapping("/debug/env")
  @Operation(summary = "Debug: Verificar variáveis de ambiente")
  public ResponseEntity<Map<String, Object>> debugEnvironment() {
    Map<String, Object> env = new HashMap<>();
    env.put("EXTERNAL_API_TOKEN_env", System.getenv("EXTERNAL_API_TOKEN"));
    env.put("EXTERNAL_API_SECRET_env", System.getenv("EXTERNAL_API_SECRET"));
    env.put("EXTERNAL_API_BASE_URL_env", System.getenv("EXTERNAL_API_BASE_URL"));

    env.put("tokenFromConfig", externalApiConfig.getToken());
    env.put("secretFromConfig", externalApiConfig.getSecret());
    env.put("baseUrlFromConfig", externalApiConfig.getBaseUrl());

    // Verificar tamanhos
    env.put("tokenEnvLength",
        System.getenv("EXTERNAL_API_TOKEN") != null ? System.getenv("EXTERNAL_API_TOKEN").length() : "null");
    env.put("secretEnvLength",
        System.getenv("EXTERNAL_API_SECRET") != null ? System.getenv("EXTERNAL_API_SECRET").length() : "null");

    return ResponseEntity.ok(env);
  }
}