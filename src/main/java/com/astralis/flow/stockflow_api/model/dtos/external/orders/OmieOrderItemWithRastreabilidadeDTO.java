package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.math.BigDecimal;

public record OmieOrderItemWithRastreabilidadeDTO(
    Long codigoProduto,
    String codigo,
    String descricao,
    Integer quantidade,
    String unidade,
    BigDecimal valorUnitario,
    BigDecimal valorTotal,
    Double pesoBruto,
    Double pesoLiquido,
    String numeroLote,
    String dataFabricacaoLote,
    String dataValidadeLote,
    Integer qtdeProdutoLote,
    String codigoAgregacaoLote) {
}
