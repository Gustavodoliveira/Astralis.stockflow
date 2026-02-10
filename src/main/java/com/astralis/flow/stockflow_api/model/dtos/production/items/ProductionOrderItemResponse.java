package com.astralis.flow.stockflow_api.model.dtos.production.items;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.astralis.flow.stockflow_api.model.enums.Kind;

public record ProductionOrderItemResponse(
    Long id,
    Long productionOrderId,
    Kind kind,
    String externalProductId,
    String productName,
    BigDecimal quantity,
    String lotCode,
    LocalDate manufacturedAt,
    LocalDate expiresAt,
    Instant createdAt,
    Instant updatedAt) {
}