package com.astralis.flow.stockflow_api.model.mappers;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.products.CreateProductSnapshotDto;
import com.astralis.flow.stockflow_api.model.dtos.products.ProductSnapshotResponse;
import com.astralis.flow.stockflow_api.model.dtos.products.UpdateProductSnapshotDto;
import com.astralis.flow.stockflow_api.model.entities.ProductSnapshot;

@Component
public class ProductSnapshotMapper {

  public ProductSnapshot toEntity(CreateProductSnapshotDto dto) {
    if (dto == null) {
      return null;
    }

    return new ProductSnapshot(
        null,
        dto.externalProductId(),
        dto.name(),
        null,
        null);
  }

  public ProductSnapshotResponse toResponse(ProductSnapshot entity) {
    if (entity == null) {
      return null;
    }

    return new ProductSnapshotResponse(
        entity.getId(),
        entity.getExternalProductId(),
        entity.getName(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public List<ProductSnapshotResponse> toResponseList(List<ProductSnapshot> entities) {
    if (entities == null) {
      return null;
    }

    return entities.stream()
        .map(this::toResponse)
        .toList();
  }

  public ProductSnapshot updateEntity(ProductSnapshot existing, UpdateProductSnapshotDto dto) {
    if (existing == null || dto == null) {
      return existing;
    }

    return new ProductSnapshot(
        existing.getId(),
        dto.externalProductId() != null ? dto.externalProductId() : existing.getExternalProductId(),
        dto.name() != null ? dto.name() : existing.getName(),
        existing.getCreatedAt(),
        Instant.now());
  }
}