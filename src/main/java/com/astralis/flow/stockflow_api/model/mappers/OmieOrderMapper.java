package com.astralis.flow.stockflow_api.model.mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.external.orders.OmieOrderDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.OmieOrderItemDTO;
import com.astralis.flow.stockflow_api.model.entities.OmieOrder;
import com.astralis.flow.stockflow_api.model.entities.OmieOrderItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OmieOrderMapper {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public List<OmieOrderDTO> fromJson(String json) {
    try {
      JsonNode root = objectMapper.readTree(json);
      JsonNode pedidos = root.path("pedido_venda_produto");
      List<OmieOrderDTO> result = new ArrayList<>();

      for (JsonNode pedido : pedidos) {
        JsonNode cabecalho = pedido.path("cabecalho");
        JsonNode frete = pedido.path("frete");
        JsonNode infoCadastro = pedido.path("infoCadastro");
        JsonNode det = pedido.path("det");

        List<OmieOrderItemDTO> itens = new ArrayList<>();
        for (JsonNode item : det) {
          JsonNode produto = item.path("produto");
          JsonNode infAdic = item.path("inf_adic");

          itens.add(new OmieOrderItemDTO(
              produto.path("codigo_produto").asLong(),
              produto.path("codigo").asText(),
              produto.path("descricao").asText(),
              produto.path("quantidade").asInt(),
              produto.path("unidade").asText(),
              new BigDecimal(produto.path("valor_unitario").asText()),
              new BigDecimal(produto.path("valor_total").asText()),
              infAdic.path("peso_bruto").asDouble(),
              infAdic.path("peso_liquido").asDouble(),
              null,
              null,
              null));
        }

        result.add(new OmieOrderDTO(
            cabecalho.path("codigo_pedido").asLong(),
            cabecalho.path("numero_pedido").asText(),
            cabecalho.path("codigo_cliente").asLong(),
            cabecalho.path("data_previsao").asText(),
            cabecalho.path("etapa").asText(),
            cabecalho.path("quantidade_itens").asInt(),
            infoCadastro.path("dInc").asText(),
            infoCadastro.path("dAlt").asText(),
            frete.path("peso_bruto").asDouble(),
            frete.path("peso_liquido").asDouble(),
            frete.path("quantidade_volumes").asInt(),
            frete.path("marca_volumes").asText(),
            frete.path("especie_volumes").asText(),
            itens));
      }

      return result;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao converter JSON Omie para OmieOrderDTO", e);
    }
  }

  public OmieOrder toEntity(OmieOrderDTO dto) {
    OmieOrder order = new OmieOrder();
    order.setCodigoPedido(dto.codigoPedido());
    order.setNumeroPedido(dto.numeroPedido());
    order.setCodigoCliente(dto.codigoCliente());
    order.setDataPrevisao(dto.dataPrevisao());
    order.setEtapa(dto.etapa());
    order.setQuantidadeItens(dto.quantidadeItens());
    order.setDataInclusao(dto.dataInclusao());
    order.setDataAlteracao(dto.dataAlteracao());
    order.setPesoBrutoTotal(dto.pesoBrutoTotal());
    order.setPesoLiquidoTotal(dto.pesoLiquidoTotal());
    order.setQuantidadeVolumes(dto.quantidadeVolumes());
    order.setMarcaVolumes(dto.marcaVolumes());
    order.setEspecieVolumes(dto.especieVolumes());

    if (dto.itens() != null) {
      List<OmieOrderItem> itens = dto.itens().stream()
          .map(itemDto -> toItemEntity(itemDto, order))
          .toList();
      order.setItens(itens);
    }

    return order;
  }

  private OmieOrderItem toItemEntity(OmieOrderItemDTO dto, OmieOrder order) {
    OmieOrderItem item = new OmieOrderItem();
    item.setOmieOrder(order);
    item.setCodigoProduto(dto.codigoProduto());
    item.setCodigo(dto.codigo());
    item.setDescricao(dto.descricao());
    item.setQuantidade(dto.quantidade());
    item.setUnidade(dto.unidade());
    item.setValorUnitario(dto.valorUnitario());
    item.setValorTotal(dto.valorTotal());
    item.setPesoBruto(dto.pesoBruto());
    item.setPesoLiquido(dto.pesoLiquido());
    return item;
  }

  public OmieOrderDTO toDTO(OmieOrder order) {
    List<OmieOrderItemDTO> itens = order.getItens() == null ? List.of()
        : order.getItens().stream()
            .map(item -> new OmieOrderItemDTO(
                item.getCodigoProduto(),
                item.getCodigo(),
                item.getDescricao(),
                item.getQuantidade(),
                item.getUnidade(),
                item.getValorUnitario(),
                item.getValorTotal(),
                item.getPesoBruto(),
                item.getPesoLiquido(),
                item.getNumeroLote(),
                item.getDataValidadeLote(),
                item.getQtdeProdutoLote()))
            .toList();

    return new OmieOrderDTO(
        order.getCodigoPedido(),
        order.getNumeroPedido(),
        order.getCodigoCliente(),
        order.getDataPrevisao(),
        order.getEtapa(),
        order.getQuantidadeItens(),
        order.getDataInclusao(),
        order.getDataAlteracao(),
        order.getPesoBrutoTotal(),
        order.getPesoLiquidoTotal(),
        order.getQuantidadeVolumes(),
        order.getMarcaVolumes(),
        order.getEspecieVolumes(),
        itens);
  }
}
