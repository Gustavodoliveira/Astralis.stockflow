package com.astralis.flow.stockflow_api.model.dtos.production;

import java.time.Instant;

public record ProductionOrderResponse(
    Long id,
    String orderNumber,
    Instant createdAt,
    Instant updatedAt) {
}