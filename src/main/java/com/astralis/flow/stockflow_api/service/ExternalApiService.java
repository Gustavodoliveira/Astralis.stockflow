package com.astralis.flow.stockflow_api.service;

import com.astralis.flow.stockflow_api.client.ExternalApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExternalApiService {

  private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
  private final ExternalApiClient apiClient;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExternalApiService(ExternalApiClient apiClient, ObjectMapper objectMapper) {
    this.apiClient = apiClient;
    this.objectMapper = objectMapper;
  }

  /**
   * Exemplo de método para buscar dados da API externa
   * 
   * @param endpoint O endpoint a ser consultado
   * @return String JSON com a resposta
   */
  public String fetchData(String endpoint) {
    logger.info("Fazendo requisição para endpoint: {}", endpoint);
    try {
      String response = apiClient.get(endpoint);
      logger.info("Resposta recebida com sucesso do endpoint: {}", endpoint);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao fazer requisição para {}: {}", endpoint, e.getMessage());
      throw e;
    }
  }

  /**
   * Exemplo de método para buscar dados com parâmetros
   * 
   * @param endpoint O endpoint a ser consultado
   * @param params   Parâmetros de query
   * @return String JSON com a resposta
   */
  public String fetchDataWithParams(String endpoint, Map<String, String> params) {
    logger.info("Fazendo requisição para endpoint: {} com parâmetros: {}", endpoint, params);
    try {
      String response = apiClient.getWithParams(endpoint, params);
      logger.info("Resposta recebida com sucesso do endpoint: {}", endpoint);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao fazer requisição para {}: {}", endpoint, e.getMessage());
      throw e;
    }
  }

  /**
   * Exemplo de método para enviar dados para a API externa
   * 
   * @param endpoint O endpoint onde enviar os dados
   * @param data     Os dados a serem enviados
   * @return String JSON com a resposta
   */
  public String sendData(String endpoint, Object data) {
    logger.info("Enviando dados para endpoint: {}", endpoint);
    try {
      String response = apiClient.post(endpoint, data);
      logger.info("Dados enviados com sucesso para endpoint: {}", endpoint);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao enviar dados para {}: {}", endpoint, e.getMessage());
      throw e;
    }
  }

  /**
   * Exemplo de método para atualizar dados na API externa
   * 
   * @param endpoint O endpoint onde atualizar os dados
   * @param data     Os dados a serem atualizados
   * @return String JSON com a resposta
   */
  public String updateData(String endpoint, Object data) {
    logger.info("Atualizando dados no endpoint: {}", endpoint);
    try {
      String response = apiClient.put(endpoint, data);
      logger.info("Dados atualizados com sucesso no endpoint: {}", endpoint);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao atualizar dados em {}: {}", endpoint, e.getMessage());
      throw e;
    }
  }

  /**
   * Exemplo de método para deletar dados da API externa
   * 
   * @param endpoint O endpoint onde deletar os dados
   * @return String JSON com a resposta
   */
  public String deleteData(String endpoint) {
    logger.info("Deletando dados do endpoint: {}", endpoint);
    try {
      String response = apiClient.delete(endpoint);
      logger.info("Dados deletados com sucesso do endpoint: {}", endpoint);
      return response;
    } catch (Exception e) {
      logger.error("Erro ao deletar dados de {}: {}", endpoint, e.getMessage());
      throw e;
    }
  }

  /**
   * Método utilitário para converter JSON string para objeto
   * 
   * @param json        O JSON string
   * @param targetClass A classe de destino
   * @return O objeto convertido
   */
  public <T> T parseJsonToObject(String json, Class<T> targetClass) {
    try {
      return objectMapper.readValue(json, targetClass);
    } catch (Exception e) {
      logger.error("Erro ao converter JSON para objeto: {}", e.getMessage());
      throw new RuntimeException("Erro ao processar resposta JSON", e);
    }
  }

  /**
   * Método utilitário para converter objeto para JSON string
   * 
   * @param object O objeto a ser convertido
   * @return String JSON
   */
  public String objectToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      logger.error("Erro ao converter objeto para JSON: {}", e.getMessage());
      throw new RuntimeException("Erro ao processar objeto para JSON", e);
    }
  }
}