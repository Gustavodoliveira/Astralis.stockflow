package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExternalOrderResponseDTO(
    UUID id,
    Long externalId,
    String numeroPedido,
    Long cliente,
    String status,
    String statusInterno,
    String xped,
    String especieVolumes,
    String qtdeVolumes,
    String dataPrevisao,
    String dataCriacao,
    String dataUltimaAtualizacao,
    BigDecimal valorFrete,
    BigDecimal valorTotal,
    String localizacao,
    UUID userId,
    List<ItemDTO> itens) {

  public record ItemDTO(
      UUID id,
      Long externalItemId,
      Long produto,
      String nomeProduto,
      String tipoProduto,
      Integer qtde,
      BigDecimal valorUnitario,
      String unidade,
      String dadosAdicionais,
      String obsItem,
      Double pesoBruto,
      Double pesoLiquido,
      String lote,
      String dataValidade,
      String localizacao) {
  }
}
