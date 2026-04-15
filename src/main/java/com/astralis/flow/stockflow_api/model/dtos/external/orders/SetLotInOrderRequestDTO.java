package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.util.List;

public record SetLotInOrderRequestDTO(
    String cCodIntPed,
    Integer nQtdVol,
    String cEspVol,
    String cMarVol,
    List<ProdutoLote> produtos) {

  public record ProdutoLote(
      String cCodIntItem,
      String lote,
      String dataValidade) {
  }
}
