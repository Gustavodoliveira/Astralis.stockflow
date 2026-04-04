package com.astralis.flow.stockflow_api.model.dtos.order_production;

import java.util.UUID;

import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;

import jakarta.validation.constraints.NotNull;

public record CreateOrderProductionRequest(

    @NotNull(message = "UserId é obrigatório") UUID userId,

    @NotNull(message = "Status de produção é obrigatório") ProductionStatus productionStatus) {
}
