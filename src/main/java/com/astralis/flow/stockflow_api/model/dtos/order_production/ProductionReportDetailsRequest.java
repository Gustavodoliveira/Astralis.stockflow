package com.astralis.flow.stockflow_api.model.dtos.order_production;

import java.time.LocalDate;

public record ProductionReportDetailsRequest(
    String requisitionNumber,
    String description,
    LocalDate startedOn,
    LocalDate completedOn,
    String responsible) {}
