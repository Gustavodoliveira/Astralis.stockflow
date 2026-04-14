package com.astralis.flow.stockflow_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.external.orders.OmieOrderDTO;
import com.astralis.flow.stockflow_api.service.OrderIntegrationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
  private final OrderIntegrationService orderIntegrationService;

  @GetMapping("/getOrders")
  public ResponseEntity<List<OmieOrderDTO>> getOrders() {
    return ResponseEntity.ok(orderIntegrationService.integrateOrder());
  }
}
