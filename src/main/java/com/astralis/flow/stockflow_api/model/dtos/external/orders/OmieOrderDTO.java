package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.util.List;

public record OmieOrderDTO(
    Long codigoPedido,
    String numeroPedido,
    Long codigoCliente,
    String dataPrevisao,
    String etapa,
    Integer quantidadeItens,
    String dataInclusao,
    String dataAlteracao,
    Double pesoBrutoTotal,
    Double pesoLiquidoTotal,
    Integer quantidadeVolumes,
    String marcaVolumes,
    String especieVolumes,
    List<OmieOrderItemDTO> itens) {
}
