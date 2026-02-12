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
        dto.identificacao(),
        dto.descricao(),
        dto.unidadeMedida(),
        dto.origem(),
        dto.pesoBruto(),
        dto.pesoLiquido(),
        dto.ncm(),
        dto.ean(),
        dto.tipo(),
        dto.versao(),
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
        entity.getIdentificacao(),
        entity.getDescricao(),
        entity.getUnidadeMedida(),
        entity.getOrigem(),
        entity.getPesoBruto(),
        entity.getPesoLiquido(),
        entity.getNcm(),
        entity.getEan(),
        entity.getTipo(),
        entity.getVersao(),
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
        dto.identificacao() != null ? dto.identificacao() : existing.getIdentificacao(),
        dto.descricao() != null ? dto.descricao() : existing.getDescricao(),
        dto.unidadeMedida() != null ? dto.unidadeMedida() : existing.getUnidadeMedida(),
        dto.origem() != null ? dto.origem() : existing.getOrigem(),
        dto.pesoBruto() != null ? dto.pesoBruto() : existing.getPesoBruto(),
        dto.pesoLiquido() != null ? dto.pesoLiquido() : existing.getPesoLiquido(),
        dto.ncm() != null ? dto.ncm() : existing.getNcm(),
        dto.ean() != null ? dto.ean() : existing.getEan(),
        dto.tipo() != null ? dto.tipo() : existing.getTipo(),
        dto.versao() != null ? dto.versao() : existing.getVersao(),
        existing.getCreatedAt(),
        Instant.now());
  }
}