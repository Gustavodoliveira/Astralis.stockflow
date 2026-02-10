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
import com.astralis.flow.stockflow_api.model.dtos.products.CreateProductSnapshotDto;
import com.astralis.flow.stockflow_api.model.dtos.products.ProductSnapshotResponse;
import com.astralis.flow.stockflow_api.model.dtos.products.UpdateProductSnapshotDto;
import com.astralis.flow.stockflow_api.service.ProductSnapshotService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/products-snapshot")
@AllArgsConstructor
public class ProductSnapshotController {
  private final ProductSnapshotService service;

  private static final Logger logger = LoggerFactory.getLogger(ProductSnapshotController.class);

  @PostMapping("/create")
  public ResponseEntity<Object> createProductSnapshot(@Valid @RequestBody CreateProductSnapshotDto dto) {
    logger.info("Criando produto snapshot com ID externo: {}", dto.externalProductId());
    try {
      var response = service.createProductSnapshot(dto);
      return ResponseEntity.ok(response);
    } catch (BussinesException e) {
      logger.warn("Erro de negócio: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<Object> getAllProductSnapshots(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    logger.info("Listando produtos snapshot - page: {}, size: {}", page, size);

    if (page != null && size != null) {
      Page<ProductSnapshotResponse> responsePage = service.getAllProductSnapshotsPageable(page, size);
      return ResponseEntity.ok(responsePage);
    } else {
      var responses = service.getAllProductSnapshots();
      return ResponseEntity.ok(responses);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getProductSnapshotById(@PathVariable Long id) {
    logger.info("Buscando produto snapshot por ID: {}", id);
    try {
      var response = service.getProductSnapshotById(id);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Produto snapshot não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/external/{externalProductId}")
  public ResponseEntity<Object> getProductSnapshotByExternalId(@PathVariable String externalProductId) {
    logger.info("Buscando produto snapshot por ID externo: {}", externalProductId);
    try {
      var response = service.getProductSnapshotByExternalId(externalProductId);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Produto snapshot não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateProductSnapshot(@PathVariable Long id,
      @Valid @RequestBody UpdateProductSnapshotDto dto) {
    logger.info("Atualizando produto snapshot com ID: {}", id);
    try {
      var response = service.updateProductSnapshot(id, dto);
      return ResponseEntity.ok(response);
    } catch (EntityNotFoundException e) {
      logger.warn("Produto snapshot não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (BussinesException e) {
      logger.warn("Erro de negócio: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteProductSnapshot(@PathVariable Long id) {
    logger.info("Deletando produto snapshot com ID: {}", id);
    try {
      service.deleteProductSnapshot(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      logger.warn("Produto snapshot não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }
}