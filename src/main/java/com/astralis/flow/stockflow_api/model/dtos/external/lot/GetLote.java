package com.astralis.flow.stockflow_api.model.dtos.external.lot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetLote(
    @JsonProperty("id") Long id,

    @JsonProperty("identificacao") String identificacao,

    @JsonProperty("produto") Long produto,

    @JsonProperty("identificacao_produto") String identificacaoProduto,

    @JsonProperty("descricao") String descricao,

    @JsonProperty("data_criacao") String dataCriacao,

    @JsonProperty("qtde") Integer qtde,

    @JsonProperty("saldo") Integer saldo,

    @JsonProperty("data_validade") String dataValidade,

    @JsonProperty("localizacao") String localizacao,

    @JsonProperty("deposito") Deposito deposito) {

}
