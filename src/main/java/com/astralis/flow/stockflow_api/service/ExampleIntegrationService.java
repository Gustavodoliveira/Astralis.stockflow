package com.astralis.flow.stockflow_api.service;

import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
public class ExampleIntegrationService {

  private static final Logger logger = LoggerFactory.getLogger(ExampleIntegrationService.class);

  private final ExternalApiClient apiClient;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExampleIntegrationService(ExternalApiClient apiClient, ObjectMapper objectMapper) {
    this.apiClient = apiClient;
    this.objectMapper = objectMapper;
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

  public String getExternalItemsByDescription(String description) {
    logger.info("Buscando itens da API externa");
    try {
      String response = apiClient
          .get("/apontamentos/estoques/lotes/lista/" + "?offset=50&page=1&filters=produtos|" + description);

      logger.info("Resposta recebida da API externa para a descrição {}: {}", description, response);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao buscar itens da API externa: {}", e.getMessage());
      throw new RuntimeException("Falha ao buscar itens", e);
    }
  }

  /**
   * Exemplo: Método utilitário para criar um payload padrão
   */
  public Map<String, Object> createProductPayload(String name, String description, double price) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("name", name);
    payload.put("description", description);
    payload.put("price", price);
    payload.put("timestamp", System.currentTimeMillis());
    return payload;
  }
}