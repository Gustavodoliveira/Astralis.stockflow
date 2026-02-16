package com.astralis.flow.stockflow_api.model.dtos.external;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de resposta para dados da API externa
 * Representa um item/produto com informações de estoque
 */
public record ExternalItemResponse(
    Long id,

    String identificacao,

    String descricao,

    @JsonProperty("data_criacao") String dataCriacao,

    Integer qtde,

    Integer saldo,

    @JsonProperty("data_validade") String dataValidade,

    String localizacao) {
}