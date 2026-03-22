package com.astralis.flow.stockflow_api.model.dtos.external.products;

import java.math.BigDecimal;

public record ProductResponseDTO(
    Long id,
    String identificacao,
    String descricao,
    String unidadeMedida,
    String tipo,
    String origem,
    BigDecimal valorVenda,
    BigDecimal valorCusto,
    String ncm,
    String status,
    String localizacao) {

}
