package com.astralis.flow.stockflow_api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.exception.BussinesException;
import com.astralis.flow.stockflow_api.model.dtos.production.CreateProductionOrderDto;
import com.astralis.flow.stockflow_api.model.dtos.production.ProductionOrderResponse;
import com.astralis.flow.stockflow_api.model.dtos.production.UpdateProductionOrderDto;
import com.astralis.flow.stockflow_api.service.ProductionOrderService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/production-orders")
@AllArgsConstructor
public class ProductionOrderController {
  private final ProductionOrderService service;

  private static final Logger logger = LoggerFactory.getLogger(ProductionOrderController.class);

  @PostMapping("/create")
  public ResponseEntity<Object> createProductionOrder(@Valid @RequestBody CreateProductionOrderDto dto) {
    logger.info("Criando ordem de produção com número: {}", dto.orderNumber());
    try {
      var response = service.createProductionOrder(dto);
      return ResponseEntity.ok(response);
    } catch (BussinesException e) {
      logger.warn("Erro de negócio: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<Object> getAllProductionOrders(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    logger.info("Listando ordens de produção - page: {}, size: {}", page, size);

    if (page != null && size != null) {
      Page<ProductionOrderResponse> responsePage = service.getAllProductionOrdersPageable(page, size);
      return ResponseEntity.ok(responsePage);
    } else {
      var responses = service.getAllProductionOrders();
      return ResponseEntity.ok(responses);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getProductionOrderById(@PathVariable Long id) {
    logger.info("Buscando ordem de produção por ID: {}", id);
    try {
      var response = service.getProductionOrderById(id);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Ordem de produção não encontrada: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/order-number/{orderNumber}")
  public ResponseEntity<Object> getProductionOrderByOrderNumber(@PathVariable String orderNumber) {
    logger.info("Buscando ordem de produção por número: {}", orderNumber);
    try {
      var response = service.getProductionOrderByOrderNumber(orderNumber);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Ordem de produção não encontrada: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateProductionOrder(@PathVariable Long id,
      @Valid @RequestBody UpdateProductionOrderDto dto) {
    logger.info("Atualizando ordem de produção com ID: {}", id);
    try {
      var response = service.updateProductionOrder(id, dto);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Ordem de produção não encontrada: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (BussinesException e) {
      logger.warn("Erro de negócio: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteProductionOrder(@PathVariable Long id) {
    logger.info("Deletando ordem de produção com ID: {}", id);
    try {
      service.deleteProductionOrder(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      logger.warn("Ordem de produção não encontrada: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }
}