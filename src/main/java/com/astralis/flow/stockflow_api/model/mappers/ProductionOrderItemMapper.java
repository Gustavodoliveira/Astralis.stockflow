package com.astralis.flow.stockflow_api.model.mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.production.items.CreateProductionOrderItemDto;
import com.astralis.flow.stockflow_api.model.dtos.production.items.ProductionOrderItemResponse;
import com.astralis.flow.stockflow_api.model.dtos.production.items.UpdateProductionOrderItemDto;
import com.astralis.flow.stockflow_api.model.entities.ProductionOrderItem;

@Component
public class ProductionOrderItemMapper {

  public ProductionOrderItem toEntity(CreateProductionOrderItemDto dto) {
    if (dto == null) {
      return null;
    }

    return new ProductionOrderItem(
        null,
        null, // productionOrder será setado no service
        dto.kind(),
        dto.externalProductId(),
        dto.productName(),
        dto.quantity() != null ? dto.quantity() : BigDecimal.ZERO,
        dto.lotCode(),
        dto.manufacturedAt(),
        dto.expiresAt(),
        null,
        null);
  }

  public ProductionOrderItemResponse toResponse(ProductionOrderItem entity) {
    if (entity == null) {
      return null;
    }

    return new ProductionOrderItemResponse(
        entity.getId(),
        entity.getProductionOrder() != null ? entity.getProductionOrder().getId() : null,
        entity.getKind(),
        entity.getExternalProductId(),
        entity.getProductName(),
        entity.getQuantity(),
        entity.getLotCode(),
        entity.getManufacturedAt(),
        entity.getExpiresAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public List<ProductionOrderItemResponse> toResponseList(List<ProductionOrderItem> entities) {
    if (entities == null) {
      return null;
    }

    return entities.stream()
        .map(this::toResponse)
        .toList();
  }

  public ProductionOrderItem updateEntity(ProductionOrderItem existing, UpdateProductionOrderItemDto dto) {
    if (existing == null || dto == null) {
      return existing;
    }

    return new ProductionOrderItem(
        existing.getId(),
        existing.getProductionOrder(),
        dto.kind() != null ? dto.kind() : existing.getKind(),
        dto.externalProductId() != null ? dto.externalProductId() : existing.getExternalProductId(),
        dto.productName() != null ? dto.productName() : existing.getProductName(),
        dto.quantity() != null ? dto.quantity() : existing.getQuantity(),
        dto.lotCode() != null ? dto.lotCode() : existing.getLotCode(),
        dto.manufacturedAt() != null ? dto.manufacturedAt() : existing.getManufacturedAt(),
        dto.expiresAt() != null ? dto.expiresAt() : existing.getExpiresAt(),
        existing.getCreatedAt(),
        Instant.now());
  }
}