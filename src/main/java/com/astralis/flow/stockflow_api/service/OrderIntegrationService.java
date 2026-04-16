package com.astralis.flow.stockflow_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.client.ClientResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.LoteResponseDto;
import com.astralis.flow.stockflow_api.model.dtos.external.products.ProductResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.ExternalOrderDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.ExternalOrderResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.GetOrdersResponse;
import com.astralis.flow.stockflow_api.model.entities.ExternalOrder;
import com.astralis.flow.stockflow_api.model.entities.ExternalOrderItem;
import com.astralis.flow.stockflow_api.model.mappers.ExternalOrderMapper;
import com.astralis.flow.stockflow_api.repository.ExternalOrderItemRepository;
import com.astralis.flow.stockflow_api.repository.ExternalOrderRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderIntegrationService {

  private static final Logger logger = LoggerFactory.getLogger(OrderIntegrationService.class);

  private final ExternalApiClient externalApiClient;
  private final ObjectMapper objectMapper;
  private final ExternalOrderRepository externalOrderRepository;
  private final ExternalOrderItemRepository externalOrderItemRepository;
  private final ExternalOrderMapper externalOrderMapper;
  private final StockIntegrationService stockIntegrationService;

  public ExternalOrderResponseDTO getOrderById(UUID orderId) {
    ExternalOrder order = externalOrderRepository.findByIdWithItens(orderId)
        .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + orderId));
    return toResponseDTO(order);
  }

  @Transactional
  public ExternalOrderResponseDTO assignSeparador(UUID orderId, UUID userId) {
    ExternalOrder order = externalOrderRepository.findByIdWithItens(orderId)
        .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + orderId));

    order.setUserId(userId);
    logger.info("Separador {} atribuído ao pedido {}", userId, orderId);

    if (order.getItens() != null) {
      for (ExternalOrderItem item : order.getItens()) {
        String produtoApiId = String.valueOf(item.getProduto());

        try {
          List<ProductResponseDTO> produtos = stockIntegrationService
              .getProductsById(produtoApiId);
          if (!produtos.isEmpty()) {
            ProductResponseDTO produto = produtos.get(0);
            item.setNomeProduto(produto.descricao());
            item.setUnidade(produto.unidadeMedida());
            item.setTipoProduto(produto.tipo());
            produtoApiId = String.valueOf(produto.id());
            logger.info("Produto {} ({}) atribuído ao item {} (id API: {})", produto.descricao(),
                produto.unidadeMedida(),
                item.getId(), produtoApiId);
          }
        } catch (Exception e) {
          logger.error("Erro ao buscar produto {} para o item {}: {}", item.getProduto(), item.getId(), e.getMessage());
        }

        try {
          List<LoteResponseDto> lotes = stockIntegrationService.getLotesByProductId(produtoApiId);
          logger.info("Lotes retornados pela API para o produto {} (id API: {}): {}", item.getProduto(), produtoApiId,
              lotes);
          LoteResponseDto lote = lotes.stream()
              .filter(l -> l.saldo() != null && l.saldo() > 0 && isLoteValido(l))
              .findFirst()
              .orElse(null);

          if (lote != null) {
            item.setLote(lote.identificacao());
            item.setDataValidade(lote.dataValidade());
            item.setLocalizacao(lote.localizacao());
            logger.info("Lote {} atribuído ao item {} (produto {})", lote.identificacao(), item.getId(),
                item.getProduto());
          } else {
            item.setLote(null);
            item.setDataValidade(null);
            item.setLocalizacao(null);
            logger.warn("Nenhum lote com saldo disponível para o produto {}", item.getProduto());
          }
        } catch (Exception e) {
          logger.error("Erro ao buscar lote para o produto {}: {}", item.getProduto(), e.getMessage());
        }

        externalOrderItemRepository.save(item);
      }
    }

    ExternalOrder saved = externalOrderRepository.save(order);
    return toResponseDTO(externalOrderRepository.findByIdWithItens(saved.getId()).get());
  }

  private ExternalOrderResponseDTO toResponseDTO(ExternalOrder order) {
    List<ExternalOrderResponseDTO.ItemDTO> itens = order.getItens() == null ? List.of()
        : order.getItens().stream()
            .map(item -> new ExternalOrderResponseDTO.ItemDTO(
                item.getId(),
                item.getExternalItemId(),
                item.getProduto(),
                item.getNomeProduto(),
                item.getTipoProduto(),
                item.getQtde(),
                item.getValorUnitario(),
                item.getUnidade(),
                item.getDadosAdicionais(),
                item.getObsItem(),
                item.getPesoBruto(),
                item.getPesoLiquido(),
                item.getLote(),
                item.getDataValidade(),
                item.getLocalizacao()))
            .toList();

    return new ExternalOrderResponseDTO(
        order.getId(),
        order.getExternalId(),
        order.getNumeroPedido(),
        order.getCliente(),
        order.getNomeCliente(),
        order.getStatus(),
        order.getStatusInterno(),
        order.getXped(),
        order.getEspecieVolumes(),
        order.getQtdeVolumes(),
        order.getDataPrevisao(),
        order.getDataCriacao(),
        order.getDataUltimaAtualizacao(),
        order.getValorFrete(),
        order.getValorTotal(),
        order.getLocalizacao(),
        order.getUserId(),
        itens);
  }

  private boolean isLoteValido(LoteResponseDto lote) {
    if (lote.dataValidade() == null || lote.dataValidade().isBlank()) {
      return true;
    }
    List<DateTimeFormatter> formatters = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    for (DateTimeFormatter formatter : formatters) {
      try {
        LocalDate validade = LocalDate.parse(lote.dataValidade(), formatter);
        boolean valido = !validade.isBefore(LocalDate.now());
        if (!valido) {
          logger.warn("Lote {} vencido em {}, ignorando", lote.identificacao(), lote.dataValidade());
        }
        return valido;
      } catch (DateTimeParseException ignored) {
      }
    }
    logger.warn("Formato de dataValidade não reconhecido para o lote {}: '{}', considerando válido",
        lote.identificacao(), lote.dataValidade());
    return true;
  }

  public List<ExternalOrder> syncOrders() {
    try {
      String json = externalApiClient.get("/comercial/pvendas/lista?offset=50&page=1&filters=status|stock");
      logger.info("Resposta bruta da API de pedidos: {}", json);
      GetOrdersResponse wrapper = objectMapper.readValue(json, GetOrdersResponse.class);
      List<ExternalOrderDTO> dtos = wrapper.response();

      return dtos.stream()
          .map(dto -> {
            ExternalOrder entity = externalOrderMapper.toEntity(dto);

            try {
              List<ClientResponseDTO> clientes = stockIntegrationService.getClientById(String.valueOf(dto.cliente()));
              if (!clientes.isEmpty()) {
                String nome = clientes.get(0).razaoSocial() != null
                    ? clientes.get(0).razaoSocial()
                    : clientes.get(0).nomeFantasia();
                entity.setNomeCliente(nome);
                logger.info("Cliente {} ({}) vinculado ao pedido {}", dto.cliente(), nome, dto.id());
              }
            } catch (Exception e) {
              logger.error("Erro ao buscar nome do cliente {} para o pedido {}: {}", dto.cliente(), dto.id(),
                  e.getMessage());
            }

            if (externalOrderRepository.existsByExternalId(dto.id())) {
              ExternalOrder existing = externalOrderRepository.findByExternalId(dto.id()).get();
              entity.setId(existing.getId());
              if (existing.getItens() != null)
                existing.getItens().clear();
              logger.info("Pedido externo {} atualizado.", dto.id());
            } else {
              logger.info("Pedido externo {} inserido.", dto.id());
            }
            return externalOrderRepository.save(entity);
          })
          .toList();
    } catch (Exception e) {
      logger.error("Erro ao sincronizar pedidos externos: {}", e.getMessage(), e);
      throw new RuntimeException("Erro ao sincronizar pedidos externos", e);
    }
  }

  public String getRawSyncOrders() {
    try {
      String json = externalApiClient.get("/comercial/pvendas/lista?offset=50&page=1&filters=status|stock");
      logger.info("Resposta bruta da API de pedidos: {}", json);
      return json;
    } catch (Exception e) {
      logger.error("Erro ao buscar resposta bruta dos pedidos: {}", e.getMessage(), e);
      throw new RuntimeException("Erro ao buscar resposta bruta dos pedidos", e);
    }
  }
}
