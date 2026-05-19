package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetOrdersResponse(
    Boolean success,
    String code,
    String message,
    String page,
    Integer total,
    List<ExternalOrderDTO> response) {
}
