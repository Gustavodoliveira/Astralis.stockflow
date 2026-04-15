package com.astralis.flow.stockflow_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.external.orders.AssignSeparadorRequestDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.ExternalOrderResponseDTO;
import com.astralis.flow.stockflow_api.model.entities.ExternalOrder;
import com.astralis.flow.stockflow_api.service.OrderIntegrationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
  private final OrderIntegrationService orderIntegrationService;

  @GetMapping("/{id}")
  public ResponseEntity<ExternalOrderResponseDTO> getOrderById(@PathVariable UUID id) {
    return ResponseEntity.ok(orderIntegrationService.getOrderById(id));
  }

  @PostMapping("/sync")
  public ResponseEntity<List<ExternalOrder>> syncOrders() {
    return ResponseEntity.ok(orderIntegrationService.syncOrders());
  }

  @PatchMapping("/{id}/separador")
  public ResponseEntity<ExternalOrderResponseDTO> assignSeparador(
      @PathVariable UUID id,
      @RequestBody AssignSeparadorRequestDTO request) {
    return ResponseEntity.ok(orderIntegrationService.assignSeparador(id, request.userId()));
  }
}
