package com.astralis.flow.stockflow_api.model.dtos.external.lot;

public record LoteResponseDto(
    Long id,
    String identificacao,
    Long produtoId,
    String identificacaoProduto,
    String descricao,
    String dataCriacao,
    Integer quantidade,
    Integer saldo,
    String dataValidade,
    String localizacao,
    String depositoDescricao) {

}
