package com.astralis.flow.stockflow_api.model.dtos.order_production;

import java.util.List;
import java.util.UUID;

import com.astralis.flow.stockflow_api.model.dtos.order_production_items.CreateOrderProductionItemPayload;
import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderProductionWithItemsRequest(

    @NotNull(message = "UserId é obrigatório") UUID userId,

    @NotNull(message = "Status de produção é obrigatório") ProductionStatus productionStatus,

    @NotEmpty(message = "A lista de itens não pode ser vazia") @Valid List<CreateOrderProductionItemPayload> items) {
}
