package com.astralis.flow.stockflow_api.service;

import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.products.GetProducts;
import com.astralis.flow.stockflow_api.model.dtos.external.products.GetProductsResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.products.ProductResponseDTO;
import com.astralis.flow.stockflow_api.model.mappers.external.ProductMapper;
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

  @Autowired
  public StockIntegrationService(ExternalApiClient apiClient, ObjectMapper objectMapper, ProductMapper productMapper) {
    this.apiClient = apiClient;
    this.objectMapper = objectMapper;
    this.productMapper = productMapper;
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

}