package com.astralis.flow.stockflow_api.model.dtos.production.items;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.astralis.flow.stockflow_api.model.enums.Kind;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductionOrderItemDto(
    @NotNull(message = "ID da ordem de produção é obrigatório") Long productionOrderId,

    @NotNull(message = "Tipo do item é obrigatório") Kind kind,

    @NotBlank(message = "ID do produto externo é obrigatório") @Size(max = 80, message = "ID do produto externo deve ter no máximo 80 caracteres") String externalProductId,

    @NotBlank(message = "Nome do produto é obrigatório") @Size(max = 120, message = "Nome do produto deve ter no máximo 120 caracteres") String productName,

    @NotNull(message = "Quantidade é obrigatória") @DecimalMin(value = "0.0", inclusive = false, message = "Quantidade deve ser maior que zero") BigDecimal quantity,

    @NotBlank(message = "Código do lote é obrigatório") @Size(max = 60, message = "Código do lote deve ter no máximo 60 caracteres") String lotCode,

    LocalDate manufacturedAt,

    LocalDate expiresAt) {
}