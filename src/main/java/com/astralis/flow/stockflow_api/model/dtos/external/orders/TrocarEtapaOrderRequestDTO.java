package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrocarEtapaOrderRequestDTO(
    @JsonProperty("cCodIntPed") String cCodIntPed) {

  private static final String ETAPA = "30";

  @JsonProperty("cEtapa")
  public String cEtapa() {
    return ETAPA;
  }
}
