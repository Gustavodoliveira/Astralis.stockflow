package com.astralis.flow.stockflow_api.model.dtos.products;

import java.time.Instant;

public record ProductSnapshotResponse(
    Long id,
    String externalProductId,
    String name,
    Instant createdAt,
    Instant updatedAt) {
}