package com.astralis.flow.stockflow_api.model.dtos.external.lot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Deposito(
    @JsonProperty("id") Long id,

    @JsonProperty("descricao") String descricao,

    @JsonProperty("mrp") String mrp) {

}
