package com.astralis.flow.stockflow_api.controller;

import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemResponse;
import com.astralis.flow.stockflow_api.service.ExampleIntegrationService;
import com.astralis.flow.stockflow_api.service.ExternalApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.examples.Example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
public class ExternalApiController {

  private final ExternalApiService externalApiService;

  private final ExampleIntegrationService exampleIntegrationService;

  @Autowired
  public ExternalApiController(ExternalApiService externalApiService,
      ExampleIntegrationService exampleIntegrationService) {
    this.externalApiService = externalApiService;
    this.exampleIntegrationService = exampleIntegrationService;
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

  @GetMapping("/getLotByDescription/{description}")
  @Operation(summary = "Buscar lotes por descrição")
  public ResponseEntity<String> getLotByDescription(@PathVariable String description) {
    try {
      String items = exampleIntegrationService.getExternalItemsByDescription(description);
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint para fazer requisição GET para a API externa
   * 
   * @param endpoint O endpoint da API externa (sem a base URL)
   * @return ResponseEntity com a resposta da API externa
   */
  @GetMapping("/proxy")
  public ResponseEntity<String> proxyGet(@RequestParam String endpoint,
      @RequestParam(required = false) Map<String, String> params) {
    try {
      String response;
      if (params != null && !params.isEmpty()) {
        response = externalApiService.fetchDataWithParams(endpoint, params);
      } else {
        response = externalApiService.fetchData(endpoint);
      }
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
  }

  /**
   * Endpoint para fazer requisição POST para a API externa
   * 
   * @param endpoint O endpoint da API externa
   * @param body     O corpo da requisição
   * @return ResponseEntity com a resposta da API externa
   */
  @PostMapping("/proxy")
  public ResponseEntity<String> proxyPost(@RequestParam String endpoint,
      @RequestBody Object body) {
    try {
      String response = externalApiService.sendData(endpoint, body);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
  }

  /**
   * Endpoint para fazer requisição PUT para a API externa
   * 
   * @param endpoint O endpoint da API externa
   * @param body     O corpo da requisição
   * @return ResponseEntity com a resposta da API externa
   */
  @PutMapping("/proxy")
  public ResponseEntity<String> proxyPut(@RequestParam String endpoint,
      @RequestBody Object body) {
    try {
      String response = externalApiService.updateData(endpoint, body);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
  }

  /**
   * Endpoint para fazer requisição DELETE para a API externa
   * 
   * @param endpoint O endpoint da API externa
   * @return ResponseEntity com a resposta da API externa
   */
  @DeleteMapping("/proxy")
  public ResponseEntity<String> proxyDelete(@RequestParam String endpoint) {
    try {
      String response = externalApiService.deleteData(endpoint);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
  }

  /**
   * Endpoint de teste para verificar se o client está funcionando
   */
  @GetMapping("/test")
  public ResponseEntity<String> testConnection() {
    try {
      // Tenta fazer uma requisição simples para testar a conectividade
      String response = externalApiService.fetchData("/");
      return ResponseEntity
          .ok("{\"status\": \"success\", \"message\": \"Conexão com API externa funcionando\", \"response\": \""
              + response + "\"}");
    } catch (Exception e) {
      return ResponseEntity
          .ok("{\"status\": \"error\", \"message\": \"Erro ao conectar com API externa: " + e.getMessage() + "\"}");
    }
  }
}