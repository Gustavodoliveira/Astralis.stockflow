package com.astralis.flow.stockflow_api.model.dtos.order_production_items;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.astralis.flow.stockflow_api.model.enums.ItemType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderProductionItemRequest(

    @NotNull(message = "ID da ordem é obrigatório") UUID orderId,

    @NotNull(message = "Tipo do item é obrigatório") ItemType itemType,

    @NotBlank(message = "ID externo do produto é obrigatório") @Size(max = 80, message = "ID externo deve ter no máximo 80 caracteres") String externalProductId,

    @NotBlank(message = "Nome do produto é obrigatório") @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres") String productName,

    @NotBlank(message = "Unidade é obrigatória") @Size(max = 20, message = "Unidade deve ter no máximo 20 caracteres") String unit,

    @NotNull(message = "Quantidade é obrigatória") @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero") BigDecimal quantity,

    @NotNull(message = "Peso unitário é obrigatório") @DecimalMin(value = "0.001", message = "Peso unitário deve ser maior que zero") BigDecimal unitWeight,

    @NotBlank(message = "Lote é obrigatório") @Size(max = 100, message = "Lote deve ter no máximo 100 caracteres") String lot,

    @NotNull(message = "Data de fabricação é obrigatória") LocalDate dateFabrication,

    @NotNull(message = "Data de validade é obrigatória") LocalDate dateValidity) {
}
