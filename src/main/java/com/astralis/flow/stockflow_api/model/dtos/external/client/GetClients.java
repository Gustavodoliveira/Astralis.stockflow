package com.astralis.flow.stockflow_api.model.dtos.external.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetClients(
    Long id,
    String identificacao,
    @JsonProperty("razao_social") String razaoSocial,
    @JsonProperty("nome_fantasia") String nomeFantasia,
    String cnpj,
    String status) {
}
