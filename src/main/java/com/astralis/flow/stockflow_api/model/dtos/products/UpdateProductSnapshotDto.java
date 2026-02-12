package com.astralis.flow.stockflow_api.model.dtos.products;

import java.math.BigDecimal;

import jakarta.validation.constraints.Size;

public record UpdateProductSnapshotDto(
    @Size(max = 80, message = "ID do produto externo deve ter no máximo 80 caracteres") String externalProductId,

    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres") String name,

    @Size(max = 40, message = "Identificação deve ter no máximo 40 caracteres") String identificacao,

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres") String descricao,

    @Size(max = 10, message = "Unidade de medida deve ter no máximo 10 caracteres") String unidadeMedida,

    @Size(max = 30, message = "Origem deve ter no máximo 30 caracteres") String origem,

    BigDecimal pesoBruto,

    BigDecimal pesoLiquido,

    @Size(max = 20, message = "NCM deve ter no máximo 20 caracteres") String ncm,

    @Size(max = 30, message = "EAN deve ter no máximo 30 caracteres") String ean,

    @Size(max = 60, message = "Tipo deve ter no máximo 60 caracteres") String tipo,

    @Size(max = 20, message = "Versão deve ter no máximo 20 caracteres") String versao) {
}