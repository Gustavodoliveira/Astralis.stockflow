package com.astralis.flow.stockflow_api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astralis.flow.stockflow_api.model.dtos.order_production.ProductionQualityCheckRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.ProductionReportDetailsRequest;
import com.astralis.flow.stockflow_api.service.ProductionReportManagementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order-production")
@RequiredArgsConstructor
public class ProductionReportController {
  private final ProductionReportManagementService reports;

  @PutMapping("/{id}/report-details")
  public ResponseEntity<Void> updateDetails(@PathVariable UUID id,
      @RequestBody ProductionReportDetailsRequest request) {
    reports.updateDetails(id, request);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/quality-checks/{itemId}")
  public ResponseEntity<Void> updateQuality(@PathVariable UUID id,
      @PathVariable UUID itemId,
      @RequestBody ProductionQualityCheckRequest request) {
    reports.updateQuality(id, itemId, request);
    return ResponseEntity.noContent().build();
  }
}
