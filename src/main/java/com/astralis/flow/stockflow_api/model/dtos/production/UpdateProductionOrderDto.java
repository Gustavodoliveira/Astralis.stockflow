package com.astralis.flow.stockflow_api.model.dtos.production;

import jakarta.validation.constraints.Size;

public record UpdateProductionOrderDto(
    @Size(max = 40, message = "Número da ordem deve ter no máximo 40 caracteres") String orderNumber) {
}