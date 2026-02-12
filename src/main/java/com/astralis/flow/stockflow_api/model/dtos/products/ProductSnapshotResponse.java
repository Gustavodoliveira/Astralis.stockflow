package com.astralis.flow.stockflow_api.model.dtos.products;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductSnapshotResponse(
    Long id,
    String externalProductId,
    String name,
    String identificacao,
    String descricao,
    String unidadeMedida,
    String origem,
    BigDecimal pesoBruto,
    BigDecimal pesoLiquido,
    String ncm,
    String ean,
    String tipo,
    String versao,
    Instant createdAt,
    Instant updatedAt) {
}