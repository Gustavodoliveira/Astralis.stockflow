package com.astralis.flow.stockflow_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.astralis.flow.stockflow_api.client.OmieApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.LoteResponseDto;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.OmieOrderDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.SetLotInOrderRequestDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.TrocarEtapaOrderRequestDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.products.ProductResponseDTO;
import com.astralis.flow.stockflow_api.model.entities.OmieOrder;
import com.astralis.flow.stockflow_api.model.mappers.OmieOrderMapper;
import com.astralis.flow.stockflow_api.repository.OmieOrderRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderIntegrationService {

  private final OmieApiClient omieApiClient;
  private final OmieOrderMapper omieOrderMapper;
  private final OmieOrderRepository omieOrderRepository;
  private final StockIntegrationService stockIntegrationService;
  private final ObjectMapper objectMapper;

  private static final Logger logger = LoggerFactory.getLogger(OrderIntegrationService.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public List<OmieOrderDTO> integrateOrder() {
    String jsonResponse = omieApiClient.call("ListarPedidos", Map.of("etapa", "20"));
    logger.info("Resposta recebida da API externa para pedidos: {}", jsonResponse);

    List<OmieOrderDTO> dtos = omieOrderMapper.fromJson(jsonResponse);
    Map<String, Long> productIdCache = new HashMap<>();

    List<OmieOrderDTO> result = new ArrayList<>();
    for (OmieOrderDTO dto : dtos) {
      OmieOrder orderEntity = omieOrderMapper.toEntity(dto);
      enrichItensWithLote(orderEntity, productIdCache);
      OmieOrder saved = saveOrUpdate(orderEntity, dto.codigoPedido());
      result.add(omieOrderMapper.toDTO(saved));
    }
    return result;
  }

  public OmieOrderDTO getOrderById(UUID id) {
    return omieOrderRepository.findById(id)
        .map(omieOrderMapper::toDTO)
        .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
  }

  private OmieOrderDTO integrateOrderByCod(String cod) {
    String jsonResponse = omieApiClient.call("ConsultarPedido", Map.of("codigo_pedido", cod));
    List<OmieOrderDTO> dtos = omieOrderMapper.fromJson(jsonResponse);
    if (dtos.isEmpty()) {
      return null;
    }
    OmieOrderDTO dto = dtos.get(0);
    OmieOrder orderEntity = omieOrderMapper.toEntity(dto);
    enrichItensWithLote(orderEntity, new HashMap<>());
    return omieOrderMapper.toDTO(orderEntity);
  }

  private OmieOrder saveOrUpdate(OmieOrder orderEntity, Long codigoPedido) {
    if (omieOrderRepository.existsByCodigoPedido(codigoPedido)) {
      logger.info("Pedido {} já existe no banco, atualizando.", codigoPedido);
      OmieOrder existing = omieOrderRepository.findByCodigoPedido(codigoPedido).get();
      orderEntity.setId(existing.getId());
      if (existing.getItens() != null)
        existing.getItens().clear();
    }
    OmieOrder saved = omieOrderRepository.save(orderEntity);
    logger.info("Pedido {} salvo no banco.", codigoPedido);
    return saved;
  }

  private String setLotInOrder(SetLotInOrderRequestDTO dto) {
    try {
      String json = objectMapper.writeValueAsString(dto.param());
      List<Map<String, Object>> params = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
      });
      return omieApiClient.call("IncluirPedCompra", params);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao serializar parâmetros para a API Omie", e);
    }
  }

  private String setOrderFat(TrocarEtapaOrderRequestDTO dto) {
    try {
      String json = objectMapper.writeValueAsString(dto);
      Map<String, Object> params = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
      });
      return omieApiClient.call("TrocarEtapaPedido", params);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao serializar parâmetros para a API Omie", e);
    }
  }

  private void enrichItensWithLote(OmieOrder order, Map<String, Long> productIdCache) {
    if (order.getItens() == null || order.getItens().isEmpty())
      return;

    order.getItens().forEach(item -> {
      try {
        String codigo = item.getCodigo();
        Long productId;

        if (productIdCache.containsKey(codigo)) {
          productId = productIdCache.get(codigo);
          logger.info("Cache hit: produto código '{}' -> id={}", codigo, productId);
        } else {
          List<ProductResponseDTO> products = stockIntegrationService.getProductsByCod(codigo);

          if (products == null || products.isEmpty()) {
            logger.warn("Nenhum produto encontrado no iApp para código '{}'", codigo);
            return;
          }

          ProductResponseDTO activeProduct = products.stream()
              .filter(p -> "ativo".equalsIgnoreCase(p.status()))
              .findFirst()
              .orElse(null);

          if (activeProduct == null) {
            logger.warn("Nenhum produto ativo encontrado no iApp para código '{}'", codigo);
            return;
          }

          productId = activeProduct.id();
          productIdCache.put(codigo, productId);
          logger.info("Cache miss: produto código '{}' consultado no iApp -> id={}", codigo, productId);
        }

        List<LoteResponseDto> lotes = stockIntegrationService.getLotesById(String.valueOf(productId));

        if (lotes == null || lotes.isEmpty()) {
          logger.warn("Nenhum lote encontrado para produtoId={}", productId);
          return;
        }

        LoteResponseDto nearestLote = lotes.stream()
            .filter(l -> l.dataValidade() != null && !l.dataValidade().isBlank())
            .filter(l -> LocalDate.parse(l.dataValidade(), DATE_FORMATTER).isAfter(LocalDate.now()))
            .min(Comparator.comparing(l -> LocalDate.parse(l.dataValidade(), DATE_FORMATTER)))
            .orElse(null);

        if (nearestLote == null) {
          logger.warn("Nenhum lote com data de validade válida para produtoId={}", productId);
          return;
        }

        item.setNumeroLote(nearestLote.identificacao());
        item.setDataValidadeLote(nearestLote.dataValidade());
        item.setQtdeProdutoLote(nearestLote.saldo());

        logger.info("Lote mais próximo salvo para produto '{}': lote={}, validade={}, saldo={}",
            codigo, nearestLote.identificacao(), nearestLote.dataValidade(), nearestLote.saldo());

      } catch (Exception e) {
        logger.warn("Erro ao buscar lote para produto '{}': {}", item.getCodigo(), e.getMessage());
      }
    });
  }

}
