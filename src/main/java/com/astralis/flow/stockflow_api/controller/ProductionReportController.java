package com.astralis.flow.stockflow_api.controller;

import java.util.UUID;
import java.util.List;
import com.astralis.flow.stockflow_api.model.entities.ProductionQualityCheck;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.ProductionQualityCheckRepository;
import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;

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
  private final OrderProductionRepository orders;
  private final ProductionQualityCheckRepository checks;

  public record DetailsResponse(Long orderNumber, String requisitionNumber, String description,
      java.time.LocalDate startedOn, java.time.LocalDate completedOn, String responsible) {}
  public record QualityResponse(UUID itemId, Boolean aspectApproved, Boolean colorApproved,
      Boolean odorApproved, Boolean samplesCollected, Boolean labelsVerified,
      String responsible, String correctiveAction) {}

  @GetMapping("/{id}/report-details")
  public DetailsResponse getDetails(@PathVariable UUID id) {
    OrderProduction order = orders.findById(id).orElseThrow(
        () -> new OrderProductionNotExist("Order not found: " + id));
    return new DetailsResponse(order.getOrderNumber(), order.getRequisitionNumber(),
        order.getDescription(), order.getStartedOn(), order.getCompletedOn(), order.getReportResponsible());
  }

  @GetMapping("/{id}/quality-checks")
  public List<QualityResponse> getQuality(@PathVariable UUID id) {
    if (!orders.existsById(id)) throw new OrderProductionNotExist("Order not found: " + id);
    return checks.findByOrderId(id).stream().map(q -> new QualityResponse(
        q.getItem().getId(),q.getAspectApproved(),q.getColorApproved(),q.getOdorApproved(),
        q.getSamplesCollected(),q.getLabelsVerified(),q.getResponsible(),q.getCorrectiveAction())).toList();
  }

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
