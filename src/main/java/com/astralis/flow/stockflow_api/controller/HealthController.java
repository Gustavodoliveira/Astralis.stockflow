package com.astralis.flow.stockflow_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.config.ExternalApiConfig;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

  @Autowired
  private ExternalApiConfig externalApiConfig;

  @GetMapping("/custom")
  public ResponseEntity<Map<String, Object>> customHealthCheck() {
    Map<String, Object> health = new HashMap<>();

    try {
      // Verificar se a aplicação está rodando
      health.put("status", "UP");
      health.put("timestamp", LocalDateTime.now());
      health.put("service", "stockflow-api");
      health.put("version", "1.0.0");

      // Verificar configurações essenciais
      Map<String, Object> checks = new HashMap<>();
      checks.put("externalApiConfigured", !externalApiConfig.getToken().equals("DEFAULT_TOKEN"));
      checks.put("baseUrl", externalApiConfig.getBaseUrl());

      health.put("checks", checks);

      return ResponseEntity.ok(health);
    } catch (Exception e) {
      health.put("status", "DOWN");
      health.put("error", e.getMessage());
      return ResponseEntity.status(503).body(health);
    }
  }

  @GetMapping("/ready")
  public ResponseEntity<String> readinessProbe() {
    // Kubernetes/AWS readiness probe
    return ResponseEntity.ok("READY");
  }

  @GetMapping("/live")
  public ResponseEntity<String> livenessProbe() {
    // Kubernetes/AWS liveness probe
    return ResponseEntity.ok("ALIVE");
  }
}