package com.astralis.flow.stockflow_api.model.dtos.production.items;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.astralis.flow.stockflow_api.model.enums.Kind;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateProductionOrderItemDto(
    Kind kind,

    @Size(max = 80, message = "ID do produto externo deve ter no máximo 80 caracteres") String externalProductId,

    @Size(max = 120, message = "Nome do produto deve ter no máximo 120 caracteres") String productName,

    @DecimalMin(value = "0.0", inclusive = false, message = "Quantidade deve ser maior que zero") BigDecimal quantity,

    @Size(max = 60, message = "Código do lote deve ter no máximo 60 caracteres") String lotCode,

    LocalDate manufacturedAt,

    LocalDate expiresAt) {
}