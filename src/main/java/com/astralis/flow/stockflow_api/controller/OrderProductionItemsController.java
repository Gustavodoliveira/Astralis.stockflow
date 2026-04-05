package com.astralis.flow.stockflow_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.order_production_items.CreateOrderProductionItemRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.service.OrderProductionItemsService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/order-production-items")
@AllArgsConstructor
public class OrderProductionItemsController {

  private final OrderProductionItemsService orderProductionItemsService;

  @PostMapping
  public ResponseEntity<List<OrderProductionItemResponse>> createItems(
      @RequestBody @Valid List<CreateOrderProductionItemRequest> dtos) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderProductionItemsService.createOrderProductionItems(dtos));
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<List<OrderProductionItemResponse>> getByOrderId(@PathVariable String orderId) {
    return ResponseEntity.ok(orderProductionItemsService.getItemsByOrderProductionId(orderId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderProductionItemResponse> getById(@PathVariable String id) {
    return ResponseEntity.ok(orderProductionItemsService.getItemById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderProductionItemResponse> updateItem(
      @PathVariable String id,
      @RequestBody @Valid CreateOrderProductionItemRequest dto) {
    return ResponseEntity.ok(orderProductionItemsService.updateOrderProductionItem(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(@PathVariable String id) {
    orderProductionItemsService.deleteItemById(id);
    return ResponseEntity.noContent().build();
  }
}
