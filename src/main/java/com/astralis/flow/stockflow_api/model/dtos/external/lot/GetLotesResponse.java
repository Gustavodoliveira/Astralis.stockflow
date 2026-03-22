package com.astralis.flow.stockflow_api.model.dtos.external.lot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO que mapeia a resposta completa da API externa para lotes
 * incluindo metadados como success, page, total e a lista de lotes
 */
public record GetLotesResponse(
    @JsonProperty("success") boolean success,

    @JsonProperty("page") String page,

    @JsonProperty("total") Integer total,

    @JsonProperty("response") List<GetLote> response) {
}