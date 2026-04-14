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

import com.astralis.flow.stockflow_api.model.dtos.external.orders.OmieOrderDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.SetLotInOrderRequestDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.TrocarEtapaOrderRequestDTO;
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

  @GetMapping("/{id}")
  public ResponseEntity<OmieOrderDTO> getOrderById(@PathVariable UUID id) {
    return ResponseEntity.ok(orderIntegrationService.getOrderById(id));
  }

  @PatchMapping("/{cCodIntPed}/etapa")
  public ResponseEntity<String> setOrderFat(@PathVariable String cCodIntPed) {
    TrocarEtapaOrderRequestDTO dto = new TrocarEtapaOrderRequestDTO(cCodIntPed);
    String response = orderIntegrationService.setOrderFat(dto);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/lote")
  public ResponseEntity<String> setLotInOrder(@RequestBody SetLotInOrderRequestDTO dto) {
    String response = orderIntegrationService.setLotInOrder(dto);
    return ResponseEntity.ok(response);
  }
}
