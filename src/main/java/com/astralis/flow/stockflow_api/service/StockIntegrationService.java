package com.astralis.flow.stockflow_api.service;

import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.products.GetProducts;
import com.astralis.flow.stockflow_api.model.dtos.external.products.GetProductsResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.products.ProductResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.GetLote;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.GetLotesResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.lot.LoteResponseDto;
import com.astralis.flow.stockflow_api.model.mappers.external.ProductMapper;
import com.astralis.flow.stockflow_api.model.mappers.external.LoteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @Autowired
  public StockIntegrationService(ExternalApiClient apiClient, ObjectMapper objectMapper,
      ProductMapper productMapper, LoteMapper loteMapper) {
    this.apiClient = apiClient;
    this.objectMapper = objectMapper;
    this.productMapper = productMapper;
    this.loteMapper = loteMapper;
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

  public List<LoteResponseDto> getLotesById(String id) {
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

}