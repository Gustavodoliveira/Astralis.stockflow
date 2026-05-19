package com.astralis.flow.stockflow_api.model.dtos.external.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetClientsResponse(
    Boolean success,
    String code,
    String message,
    GetClients response) {
}
