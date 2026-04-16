package com.astralis.flow.stockflow_api.model.dtos.order_production;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;

public record OrderProductionWithItemsResponse(
    UUID id,
    UUID userId,
    String userName,
    ProductionStatus productionStatus,
    Instant createdAt,
    Instant updatedAt,
    List<OrderProductionItemResponse> items) {
}
