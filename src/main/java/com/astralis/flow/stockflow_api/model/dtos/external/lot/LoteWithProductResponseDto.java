package com.astralis.flow.stockflow_api.model.dtos.external.lot;

public record LoteWithProductResponseDto(
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
    String depositoDescricao,
    String produtoNome) {

  public static LoteWithProductResponseDto from(LoteResponseDto lote, String produtoNome) {
    return new LoteWithProductResponseDto(
        lote.id(),
        lote.identificacao(),
        lote.produtoId(),
        lote.identificacaoProduto(),
        lote.descricao(),
        lote.dataCriacao(),
        lote.quantidade(),
        lote.saldo(),
        lote.dataValidade(),
        lote.localizacao(),
        lote.depositoDescricao(),
        produtoNome);
  }
}
