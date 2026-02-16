package com.astralis.flow.stockflow_api.service;

import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemTypedResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service para trabalhar com itens da API externa
 * Demonstra como usar os DTOs criados
 */
@Service
public class ExternalItemService {

  private static final Logger logger = LoggerFactory.getLogger(ExternalItemService.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final ExternalApiClient apiClient;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExternalItemService(ExternalApiClient apiClient, ObjectMapper objectMapper) {
    this.apiClient = apiClient;
    this.objectMapper = objectMapper;
  }

  /**
   * Busca um item específico da API externa
   * 
   * @param itemId ID do item
   * @return ExternalItemResponse com os dados do item
   */
  public ExternalItemResponse getItem(Long itemId) {
    logger.info("Buscando item com ID: {}", itemId);

    try {
      String response = apiClient.get("/items/" + itemId);
      return objectMapper.readValue(response, ExternalItemResponse.class);
    } catch (Exception e) {
      logger.error("Erro ao buscar item {}: {}", itemId, e.getMessage());
      throw new RuntimeException("Falha ao buscar item", e);
    }
  }

  /**
   * Busca lista de itens da API externa
   * 
   * @return Lista de ExternalItemResponse
   */
  public List<ExternalItemResponse> getAllItems() {
    logger.info("Buscando todos os itens...");

    try {
      String response = apiClient.get("/items");
      TypeReference<List<ExternalItemResponse>> typeRef = new TypeReference<List<ExternalItemResponse>>() {
      };
      return objectMapper.readValue(response, typeRef);
    } catch (Exception e) {
      logger.error("Erro ao buscar itens: {}", e.getMessage());
      throw new RuntimeException("Falha ao buscar itens", e);
    }
  }

  /**
   * Busca itens com filtros
   * 
   * @param identificacao Filtro por identificação
   * @param qtdeMinima    Quantidade mínima
   * @return Lista filtrada de itens
   */
  public List<ExternalItemResponse> searchItems(String identificacao, Integer qtdeMinima) {
    logger.info("Buscando itens com filtros - identificacao: {}, qtdeMinima: {}", identificacao, qtdeMinima);

    try {
      Map<String, String> params = Map.of(
          "identificacao", identificacao != null ? identificacao : "",
          "qtde_minima", qtdeMinima != null ? qtdeMinima.toString() : "0");

      String response = apiClient.getWithParams("/items/search", params);
      TypeReference<List<ExternalItemResponse>> typeRef = new TypeReference<List<ExternalItemResponse>>() {
      };
      return objectMapper.readValue(response, typeRef);
    } catch (Exception e) {
      logger.error("Erro ao buscar itens com filtros: {}", e.getMessage());
      throw new RuntimeException("Falha ao buscar itens filtrados", e);
    }
  }

  /**
   * Converte ExternalItemResponse para ExternalItemTypedResponse
   * 
   * @param item Item com strings para datas
   * @return Item com datas tipadas como LocalDateTime
   */
  public ExternalItemTypedResponse convertToTypedResponse(ExternalItemResponse item) {
    try {
      LocalDateTime dataCriacao = item.dataCriacao() != null
          ? LocalDateTime.parse(item.dataCriacao(), DATE_FORMATTER)
          : null;

      LocalDateTime dataValidade = item.dataValidade() != null
          ? LocalDateTime.parse(item.dataValidade(), DATE_FORMATTER)
          : null;

      return new ExternalItemTypedResponse(
          item.id(),
          item.identificacao(),
          item.descricao(),
          dataCriacao,
          item.qtde(),
          item.saldo(),
          dataValidade,
          item.localizacao());
    } catch (Exception e) {
      logger.error("Erro ao converter item para tipado: {}", e.getMessage());
      throw new RuntimeException("Falha na conversão de data", e);
    }
  }

  /**
   * Busca item e retorna versão tipada
   * 
   * @param itemId ID do item
   * @return ExternalItemTypedResponse com datas como LocalDateTime
   */
  public ExternalItemTypedResponse getTypedItem(Long itemId) {
    ExternalItemResponse item = getItem(itemId);
    return convertToTypedResponse(item);
  }

  /**
   * Verifica se um item está no estoque (qtde > 0)
   * 
   * @param item Item a verificar
   * @return true se tem estoque, false caso contrário
   */
  public boolean hasStock(ExternalItemResponse item) {
    return item.qtde() != null && item.qtde() > 0;
  }

  /**
   * Verifica se um item está vencido baseado na data de validade
   * 
   * @param item Item a verificar
   * @return true se vencido, false caso contrário
   */
  public boolean isExpired(ExternalItemTypedResponse item) {
    if (item.dataValidade() == null) {
      return false;
    }
    return item.dataValidade().isBefore(LocalDateTime.now());
  }

  /**
   * Cria um novo item na API externa
   * 
   * @param identificacao Identificação do item
   * @param descricao     Descrição do item
   * @param qtde          Quantidade
   * @param localizacao   Localização (opcional)
   * @return Item criado
   */
  public ExternalItemResponse createItem(String identificacao, String descricao, Integer qtde, String localizacao) {
    logger.info("Criando novo item: {}", identificacao);

    try {
      Map<String, Object> itemData = Map.of(
          "identificacao", identificacao,
          "descricao", descricao,
          "qtde", qtde,
          "saldo", qtde, // Assumindo que saldo inicial = quantidade
          "localizacao", localizacao != null ? localizacao : "");

      String response = apiClient.post("/items", itemData);
      return objectMapper.readValue(response, ExternalItemResponse.class);
    } catch (Exception e) {
      logger.error("Erro ao criar item: {}", e.getMessage());
      throw new RuntimeException("Falha ao criar item", e);
    }
  }

  /**
   * Atualiza a quantidade de um item
   * 
   * @param itemId         ID do item
   * @param novaQuantidade Nova quantidade
   * @return Item atualizado
   */
  public ExternalItemResponse updateQuantity(Long itemId, Integer novaQuantidade) {
    logger.info("Atualizando quantidade do item {}: {}", itemId, novaQuantidade);

    try {
      Map<String, Object> updateData = Map.of(
          "qtde", novaQuantidade,
          "saldo", novaQuantidade);

      String response = apiClient.put("/items/" + itemId, updateData);
      return objectMapper.readValue(response, ExternalItemResponse.class);
    } catch (Exception e) {
      logger.error("Erro ao atualizar quantidade do item {}: {}", itemId, e.getMessage());
      throw new RuntimeException("Falha ao atualizar quantidade", e);
    }
  }
}