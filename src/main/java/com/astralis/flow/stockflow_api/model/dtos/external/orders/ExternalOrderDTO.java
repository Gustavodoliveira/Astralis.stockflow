package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExternalOrderDTO(
    Long id,

    @JsonProperty("numero_pedido") String numeroPedido,

    Long cliente,

    String status,

    String xped,

    Volumes volumes,

    Datas datas,

    Valores valores,

    List<Produto> produtos) {

  public record Volumes(
      @JsonProperty("especie_volumes") String especieVolumes,
      @JsonProperty("qtde_volumes") String qtdeVolumes) {
  }

  public record Datas(
      @JsonProperty("data_previsao") String dataPrevisao,
      @JsonProperty("data_criacao") String dataCriacao,
      @JsonProperty("data_ultima_atualizacao") String dataUltimaAtualizacao) {
  }

  public record Valores(
      @JsonProperty("valor_frete") BigDecimal valorFrete,
      @JsonProperty("valor_total") BigDecimal valorTotal) {
  }

  public record Produto(
      Long id,

      Long produto,

      Integer qtde,

      @JsonProperty("valor_unitario") BigDecimal valorUnitario,

      @JsonProperty("dados_adicionais") String dadosAdicionais,

      @JsonProperty("obs_item") String obsItem,

      @JsonProperty("peso_bruto") Double pesoBruto,

      @JsonProperty("peso_liquido") Double pesoLiquido,

      String unidade,

      String lote,

      @JsonProperty("data_validade") String dataValidade) {
  }
}
