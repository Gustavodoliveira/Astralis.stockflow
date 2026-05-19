package com.astralis.flow.stockflow_api.service;

import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.client.ClientResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.client.GetClientsResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.products.GetProductsResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.products.ProductResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.GetLotesResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.LoteResponseDto;
import com.astralis.flow.stockflow_api.model.mappers.external.ClientMapper;
import com.astralis.flow.stockflow_api.model.mappers.external.ProductMapper;
import com.astralis.flow.stockflow_api.model.mappers.external.LoteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.model.dtos.external.lot.LoteWithProductResponseDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exemplo de service específico que usa o ExternalApiClient
 * Este service demonstra como implementar funcionalidades específicas
 * usando a API externa
 */
@Service
public class StockIntegrationService {

  private static final Logger logger = LoggerFactory.getLogger(StockIntegrationService.class);

  private final ExternalApiClient apiClient;
  private final ObjectMapper objectMapper;
  private final ProductMapper productMapper;
  private final LoteMapper loteMapper;
  private final ClientMapper clientMapper;

  @Autowired
  public StockIntegrationService(ExternalApiClient apiClient, ObjectMapper objectMapper,
      ProductMapper productMapper, LoteMapper loteMapper, ClientMapper clientMapper) {
    this.apiClient = apiClient;
    this.objectMapper = objectMapper;
    this.productMapper = productMapper;
    this.loteMapper = loteMapper;
    this.clientMapper = clientMapper;
  }

  public String getExternalItemsByLocation(String localização) {
    logger.info("Buscando itens da API externa");
    try {
      String response = apiClient
          .get("/apontamentos/estoques/lotes/lista/" + "?offset=50&page=1&filters=localizacao|" + localização);

      logger.info("Resposta recebida da API externa para a localização {}: {}", localização, response);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao buscar itens da API externa: {}", e.getMessage());
      throw new RuntimeException("Falha ao buscar itens", e);
    }
  }

  public String getExternalItemsLotById(String id) {
    logger.info("Buscando itens da API externa");
    try {
      String response = apiClient
          .get("/apontamentos/estoques/lotes/lista/" + "?offset=50&page=1&filters=produtos|" + id);

      logger.info("Resposta recebida da API externa para o ID {}: {}", id, response);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao buscar itens da API externa: {}", e.getMessage());
      throw new RuntimeException("Falha ao buscar itens", e);
    }
  }

  public List<ProductResponseDTO> getProductsByCod(String cod) {
    logger.info("Buscando produtos da API externa por código: {}", cod);

    try {
      String jsonResponse = apiClient
          .get("/engenharia/produtos/lista?offset=50&page=1&filters=identificacao|" + cod);

      // Mapear resposta completa da API externa
      GetProductsResponse fullResponse = objectMapper.readValue(jsonResponse, GetProductsResponse.class);

      // Converter para DTOs limpos da nossa API
      List<ProductResponseDTO> cleanProducts = productMapper.toResponseDTOList(fullResponse.response());

      return cleanProducts;

    } catch (Exception e) {
      logger.error("Erro ao buscar produtos por código '{}': {}", cod, e.getMessage());
      throw new RuntimeException("Falha ao buscar produtos por código", e);
    }
  }

  public List<ProductResponseDTO> getProductsById(String id) {
    logger.info("Buscando produtos da API externa por ID: {}", id);

    try {
      String jsonResponse = apiClient
          .get("/engenharia/produtos/lista?offset=50&page=1&filters=id|" + id);

      // Mapear resposta completa da API externa
      GetProductsResponse fullResponse = objectMapper.readValue(jsonResponse, GetProductsResponse.class);

      // Converter para DTOs limpos da nossa API
      List<ProductResponseDTO> cleanProducts = productMapper.toResponseDTOList(fullResponse.response());

      return cleanProducts;

    } catch (Exception e) {
      logger.error("Erro ao buscar produtos por ID '{}': {}", id, e.getMessage());
      throw new RuntimeException("Falha ao buscar produtos por ID", e);
    }
  }

  public List<ProductResponseDTO> getProductsByDescription(String description) {
    logger.info("Buscando produtos da API externa por descrição: {}", description);

    try {
      String jsonResponse = apiClient
          .get("/engenharia/produtos/lista?offset=50&page=1&filters=descricao|" + description);

      // Mapear resposta completa da API externa
      GetProductsResponse fullResponse = objectMapper.readValue(jsonResponse, GetProductsResponse.class);

      // Converter para DTOs limpos da nossa API
      List<ProductResponseDTO> cleanProducts = productMapper.toResponseDTOList(fullResponse.response());

      return cleanProducts;

    } catch (Exception e) {
      logger.error("Erro ao buscar produtos por descrição '{}': {}", description, e.getMessage());
      throw new RuntimeException("Falha ao buscar produtos por descrição", e);
    }
  }

  public List<LoteResponseDto> getLotesByLocation(String localizacao) {
    logger.info("Buscando lotes da API externa por localização: {}", localizacao);
    try {
      String jsonResponse = apiClient
          .get("/apontamentos/estoques/lotes/lista/?offset=50&page=1&filters=localizacao|" + localizacao);

      // Mapear resposta completa da API externa
      GetLotesResponse fullResponse = objectMapper.readValue(jsonResponse, GetLotesResponse.class);

      // Converter para DTOs limpos da nossa API
      List<LoteResponseDto> cleanLotes = loteMapper.toResponseDTOList(fullResponse.response());

      logger.info("Convertidos {} lotes para resposta limpa", cleanLotes.size());
      return cleanLotes;
    } catch (Exception e) {
      logger.error("Erro ao buscar lotes por localização '{}': {}", localizacao, e.getMessage());
      throw new RuntimeException("Falha ao buscar lotes por localização", e);
    }
  }

  public List<LoteWithProductResponseDto> getLotesByLocationWithProduct(String localizacao) {
    logger.info("Buscando lotes com produto da API externa por localização: {}", localizacao);
    try {
      List<LoteResponseDto> lotes = getLotesByLocation(localizacao);

      return lotes.stream().map(lote -> {
        String produtoNome = null;
        if (lote.produtoId() != null) {
          try {
            List<ProductResponseDTO> produtos = getProductsById(String.valueOf(lote.produtoId()));
            if (!produtos.isEmpty()) {
              produtoNome = produtos.get(0).descricao();
            }
          } catch (Exception ex) {
            logger.warn("Não foi possível buscar produto id={}: {}", lote.produtoId(), ex.getMessage());
          }
        }
        return LoteWithProductResponseDto.from(lote, produtoNome);
      }).collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("Erro ao buscar lotes com produto por localização '{}': {}", localizacao, e.getMessage());
      throw new RuntimeException("Falha ao buscar lotes com produto por localização", e);
    }
  }

  public List<LoteResponseDto> getLotesByProductId(String id) {
    logger.info("Buscando lotes da API externa por ID do produto: {}", id);
    try {
      String jsonResponse = apiClient
          .get("/apontamentos/estoques/lotes/lista/?offset=50&page=1&filters=produtos|" + id);

      // Mapear resposta completa da API externa
      GetLotesResponse fullResponse = objectMapper.readValue(jsonResponse, GetLotesResponse.class);

      // Converter para DTOs limpos da nossa API
      List<LoteResponseDto> cleanLotes = loteMapper.toResponseDTOList(fullResponse.response());

      logger.info("Convertidos {} lotes para resposta limpa", cleanLotes.size());
      return cleanLotes;
    } catch (Exception e) {
      logger.error("Erro ao buscar lotes por ID do produto '{}': {}", id, e.getMessage());
      throw new RuntimeException("Falha ao buscar lotes por ID do produto", e);
    }
  }

  public List<LoteResponseDto> getLotesById(String id) {
    logger.info("Buscando lotes da API externa por ID do produto: {}", id);
    try {
      String jsonResponse = apiClient
          .get("/apontamentos/estoques/lotes/busca/" + id);

      // Mapear resposta completa da API externa
      GetLotesResponse fullResponse = objectMapper.readValue(jsonResponse, GetLotesResponse.class);

      // Converter para DTOs limpos da nossa API
      List<LoteResponseDto> cleanLotes = loteMapper.toResponseDTOList(fullResponse.response());

      logger.info("Convertidos {} lotes para resposta limpa", cleanLotes.size());
      return cleanLotes;
    } catch (Exception e) {
      logger.error("Erro ao buscar lotes por ID do produto '{}': {}", id, e.getMessage());
      throw new RuntimeException("Falha ao buscar lotes por ID do produto", e);
    }
  }

  public List<ClientResponseDTO> getClientById(String id) {
    logger.info("Buscando cliente da API externa por ID: {}", id);
    try {
      String jsonResponse = apiClient
          .get("/comercial/clientes/busca/" + id);

      logger.info("Resposta bruta da API de clientes para id {}: {}", id, jsonResponse);

      GetClientsResponse fullResponse = objectMapper.readValue(jsonResponse, GetClientsResponse.class);

      if (Boolean.FALSE.equals(fullResponse.success())) {
        logger.error("API externa retornou erro ao buscar cliente {}: [{}] {}", id, fullResponse.code(),
            fullResponse.message());
        return List.of();
      }

      List<ClientResponseDTO> clients = fullResponse.response() != null
          ? List.of(clientMapper.toResponseDTO(fullResponse.response()))
          : List.of();

      logger.info("Cliente(s) retornado(s) para id {}: {}", id, clients.size());
      return clients;
    } catch (Exception e) {
      logger.error("Erro ao buscar cliente por ID '{}': {}", id, e.getMessage());
      throw new RuntimeException("Falha ao buscar cliente por ID", e);
    }
  }

}