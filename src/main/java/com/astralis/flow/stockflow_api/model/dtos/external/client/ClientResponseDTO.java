package com.astralis.flow.stockflow_api.model.dtos.external.client;

public record ClientResponseDTO(
    Long id,
    String identificacao,
    String razaoSocial,
    String nomeFantasia,
    String cnpj,
    String status) {
}
