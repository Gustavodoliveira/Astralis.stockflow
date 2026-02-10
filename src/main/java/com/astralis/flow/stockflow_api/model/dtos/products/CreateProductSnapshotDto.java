package com.astralis.flow.stockflow_api.model.dtos.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductSnapshotDto(
    @NotBlank(message = "ID do produto externo é obrigatório") @Size(max = 80, message = "ID do produto externo deve ter no máximo 80 caracteres") String externalProductId,

    @NotBlank(message = "Nome é obrigatório") @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres") String name) {
}