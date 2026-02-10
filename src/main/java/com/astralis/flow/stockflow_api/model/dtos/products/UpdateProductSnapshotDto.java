package com.astralis.flow.stockflow_api.model.dtos.products;

import jakarta.validation.constraints.Size;

public record UpdateProductSnapshotDto(
    @Size(max = 80, message = "ID do produto externo deve ter no máximo 80 caracteres") String externalProductId,

    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres") String name) {
}