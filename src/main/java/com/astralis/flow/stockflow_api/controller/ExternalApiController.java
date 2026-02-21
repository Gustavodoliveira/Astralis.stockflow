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

}