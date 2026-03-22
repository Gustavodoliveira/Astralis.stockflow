package com.astralis.flow.stockflow_api.model.mappers.external;

import com.astralis.flow.stockflow_api.model.dtos.external.lot.GetLote;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.LoteResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoteMapper {

  /**
   * Converte GetLote (DTO da API externa) para LoteResponseDto (DTO da nossa API)
   */
  public LoteResponseDto toResponseDTO(GetLote lote) {
    return new LoteResponseDto(
        lote.id(),
        lote.identificacao(),
        lote.produto(),
        lote.identificacaoProduto(),
        lote.descricao(),
        lote.dataCriacao(),
        lote.qtde(),
        lote.saldo(),
        lote.dataValidade(),
        lote.localizacao(),
        lote.deposito() != null ? lote.deposito().descricao() : null);
  }

  /**
   * Converte uma lista de GetLote para lista de LoteResponseDto
   */
  public List<LoteResponseDto> toResponseDTOList(List<GetLote> lotes) {
    return lotes.stream()
        .map(this::toResponseDTO)
        .toList();
  }
}