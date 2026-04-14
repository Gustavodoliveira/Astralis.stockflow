package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SetLotInOrderRequestDTO(
    @JsonProperty("param") List<Param> param) {

  public record Param(
      @JsonProperty("cabecalho_incluir") Cabecalho cabecalhoIncluir,
      @JsonProperty("frete_incluir") Frete freteIncluir,
      @JsonProperty("produtos_incluir") List<Produto> produtosIncluir) {
  }

  public record Cabecalho(
      @JsonProperty("cCodIntPed") String cCodIntPed) {
  }

  public record Frete(
      @JsonProperty("nQtdVol") Integer nQtdVol,
      @JsonProperty("cEspVol") String cEspVol,
      @JsonProperty("cMarVol") String cMarVol) {
  }

  public record Produto(
      @JsonProperty("cCodIntItem") String cCodIntItem,
      @JsonProperty("rastreabilidade") Rastreabilidade rastreabilidade) {
  }

  public record Rastreabilidade(
      @JsonProperty("lote") String lote,
      @JsonProperty("data_validade") String dataValidade) {
  }
}
