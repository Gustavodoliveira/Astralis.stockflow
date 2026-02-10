package com.astralis.flow.stockflow_api.model.mappers;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.production.CreateProductionOrderDto;
import com.astralis.flow.stockflow_api.model.dtos.production.ProductionOrderResponse;
import com.astralis.flow.stockflow_api.model.dtos.production.UpdateProductionOrderDto;
import com.astralis.flow.stockflow_api.model.entities.ProductionOrder;

@Component
public class ProductionOrderMapper {

  public ProductionOrder toEntity(CreateProductionOrderDto dto) {
    if (dto == null) {
      return null;
    }

    return new ProductionOrder(
        null,
        dto.orderNumber(),
        null, // items serão adicionados posteriormente
        null,
        null);
  }

  public ProductionOrderResponse toResponse(ProductionOrder entity) {
    if (entity == null) {
      return null;
    }

    return new ProductionOrderResponse(
        entity.getId(),
        entity.getOrderNumber(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public List<ProductionOrderResponse> toResponseList(List<ProductionOrder> entities) {
    if (entities == null) {
      return null;
    }

    return entities.stream()
        .map(this::toResponse)
        .toList();
  }

  public ProductionOrder updateEntity(ProductionOrder existing, UpdateProductionOrderDto dto) {
    if (existing == null || dto == null) {
      return existing;
    }

    return new ProductionOrder(
        existing.getId(),
        dto.orderNumber() != null ? dto.orderNumber() : existing.getOrderNumber(),
        existing.getItems(),
        existing.getCreatedAt(),
        Instant.now());
  }
}