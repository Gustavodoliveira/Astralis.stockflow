package com.astralis.flow.stockflow_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.service.OrderProductionService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/order-production")
@AllArgsConstructor
public class OrderProductionController {

  private final OrderProductionService orderProductionService;

  @PostMapping("/create")
  public ResponseEntity<OrderProductionResponse> createOrderProduction(CreateOrderProductionRequest orderProd) {
    OrderProductionResponse response = orderProductionService.createOrderProduction(orderProd);
    return ResponseEntity.ok(response);

  }

  @GetMapping("/get/{id}")
  public ResponseEntity<OrderProductionResponse> getOrderProductionById(String id) {
    OrderProductionResponse response = orderProductionService.getOrderProductionById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getAll")
  public ResponseEntity<List<OrderProductionResponse>> getAllOrderProductions() {
    List<OrderProductionResponse> response = orderProductionService.getAllOrderProductions();
    return ResponseEntity.ok(response);

  }

  @PutMapping("/update/{id}")
  public ResponseEntity<OrderProductionResponse> updateOrderProduction(String id,
      CreateOrderProductionRequest orderProd) {
    OrderProductionResponse response = orderProductionService.updateOrderProduction(id, orderProd);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteOrderProductionById(String id) {
    orderProductionService.deleteOrderProductionById(id);
    return ResponseEntity.ok().body("Deletado com sucesso");
  }
}
