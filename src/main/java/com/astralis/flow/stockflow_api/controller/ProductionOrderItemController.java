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

import com.astralis.flow.stockflow_api.model.dtos.production.items.CreateProductionOrderItemDto;
import com.astralis.flow.stockflow_api.model.dtos.production.items.ProductionOrderItemResponse;
import com.astralis.flow.stockflow_api.model.dtos.production.items.UpdateProductionOrderItemDto;
import com.astralis.flow.stockflow_api.model.enums.Kind;
import com.astralis.flow.stockflow_api.service.ProductionOrderItemService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/production-order-items")
@AllArgsConstructor
public class ProductionOrderItemController {
  private final ProductionOrderItemService service;

  private static final Logger logger = LoggerFactory.getLogger(ProductionOrderItemController.class);

  @PostMapping("/create")
  public ResponseEntity<Object> createProductionOrderItem(@Valid @RequestBody CreateProductionOrderItemDto dto) {
    logger.info("Criando item da ordem de produção para ordem ID: {}", dto.productionOrderId());
    try {
      var response = service.createProductionOrderItem(dto);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Ordem de produção não encontrada: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<Object> getAllProductionOrderItems(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    logger.info("Listando itens das ordens de produção - page: {}, size: {}", page, size);

    if (page != null && size != null) {
      Page<ProductionOrderItemResponse> responsePage = service.getAllProductionOrderItemsPageable(page, size);
      return ResponseEntity.ok(responsePage);
    } else {
      var responses = service.getAllProductionOrderItems();
      return ResponseEntity.ok(responses);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getProductionOrderItemById(@PathVariable Long id) {
    logger.info("Buscando item da ordem de produção por ID: {}", id);
    try {
      var response = service.getProductionOrderItemById(id);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Item da ordem de produção não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/by-production-order/{productionOrderId}")
  public ResponseEntity<Object> getItemsByProductionOrderId(@PathVariable Long productionOrderId) {
    logger.info("Buscando itens por ordem de produção ID: {}", productionOrderId);
    var responses = service.getItemsByProductionOrderId(productionOrderId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-external-product/{externalProductId}")
  public ResponseEntity<Object> getItemsByExternalProductId(@PathVariable String externalProductId) {
    logger.info("Buscando itens por produto externo ID: {}", externalProductId);
    var responses = service.getItemsByExternalProductId(externalProductId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-kind/{kind}")
  public ResponseEntity<Object> getItemsByKind(@PathVariable Kind kind) {
    logger.info("Buscando itens por tipo: {}", kind);
    var responses = service.getItemsByKind(kind);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-lot/{lotCode}")
  public ResponseEntity<Object> getItemsByLotCode(@PathVariable String lotCode) {
    logger.info("Buscando itens por lote: {}", lotCode);
    var responses = service.getItemsByLotCode(lotCode);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-production-order-and-kind/{productionOrderId}/{kind}")
  public ResponseEntity<Object> getItemsByProductionOrderIdAndKind(@PathVariable Long productionOrderId,
      @PathVariable Kind kind) {
    logger.info("Buscando itens por ordem de produção ID: {} e tipo: {}", productionOrderId, kind);
    var responses = service.getItemsByProductionOrderIdAndKind(productionOrderId, kind);
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateProductionOrderItem(@PathVariable Long id,
      @Valid @RequestBody UpdateProductionOrderItemDto dto) {
    logger.info("Atualizando item da ordem de produção com ID: {}", id);
    try {
      var response = service.updateProductionOrderItem(id, dto);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Item da ordem de produção não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteProductionOrderItem(@PathVariable Long id) {
    logger.info("Deletando item da ordem de produção com ID: {}", id);
    try {
      service.deleteProductionOrderItem(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      logger.warn("Item da ordem de produção não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }
}