package com.astralis.flow.stockflow_api.model.dtos.order_production;

public record ProductionQualityCheckRequest(
    Boolean aspectApproved,
    Boolean colorApproved,
    Boolean odorApproved,
    Boolean samplesCollected,
    Boolean labelsVerified,
    String responsible,
    String correctiveAction) {}
