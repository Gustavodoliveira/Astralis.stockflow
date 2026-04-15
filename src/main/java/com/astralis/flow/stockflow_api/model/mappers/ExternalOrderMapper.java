package com.astralis.flow.stockflow_api.model.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.external.orders.ExternalOrderDTO;
import com.astralis.flow.stockflow_api.model.entities.ExternalOrder;
import com.astralis.flow.stockflow_api.model.entities.ExternalOrderItem;

@Component
public class ExternalOrderMapper {

  public ExternalOrder toEntity(ExternalOrderDTO dto) {
    ExternalOrder order = new ExternalOrder();
    order.setExternalId(dto.id());
    order.setNumeroPedido(dto.numeroPedido());
    order.setCliente(dto.cliente());
    order.setStatus(dto.status());
    order.setXped(dto.xped());

    if (dto.volumes() != null) {
      order.setEspecieVolumes(dto.volumes().especieVolumes());
      order.setQtdeVolumes(dto.volumes().qtdeVolumes());
    }

    if (dto.datas() != null) {
      order.setDataPrevisao(dto.datas().dataPrevisao());
      order.setDataCriacao(dto.datas().dataCriacao());
      order.setDataUltimaAtualizacao(dto.datas().dataUltimaAtualizacao());
    }

    if (dto.valores() != null) {
      order.setValorFrete(dto.valores().valorFrete());
      order.setValorTotal(dto.valores().valorTotal());
    }

    if (dto.produtos() != null) {
      List<ExternalOrderItem> itens = dto.produtos().stream()
          .map(p -> toItemEntity(p, order))
          .toList();
      order.setItens(itens);
    }

    return order;
  }

  private ExternalOrderItem toItemEntity(ExternalOrderDTO.Produto dto, ExternalOrder order) {
    ExternalOrderItem item = new ExternalOrderItem();
    item.setExternalOrder(order);
    item.setExternalItemId(dto.id());
    item.setProduto(dto.produto());
    item.setQtde(dto.qtde());
    item.setValorUnitario(dto.valorUnitario());
    item.setDadosAdicionais(dto.dadosAdicionais());
    item.setObsItem(dto.obsItem());
    item.setPesoBruto(dto.pesoBruto());
    item.setPesoLiquido(dto.pesoLiquido());
    item.setUnidade(dto.unidade());
    item.setLote(dto.lote());
    item.setDataValidade(dto.dataValidade());
    return item;
  }
}
