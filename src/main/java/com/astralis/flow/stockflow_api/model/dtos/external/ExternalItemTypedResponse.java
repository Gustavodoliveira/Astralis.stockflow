package com.astralis.flow.stockflow_api.model.dtos.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO de resposta para dados da API externa com tipos tipados
 * Representa um item/produto com informações de estoque
 * Versão com datas como LocalDateTime
 */
public record ExternalItemTypedResponse(
    Long id,

    String identificacao,

    String descricao,

    @JsonProperty("data_criacao") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dataCriacao,

    Integer qtde,

    Integer saldo,

    @JsonProperty("data_validade") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dataValidade,

    String localizacao) {
}