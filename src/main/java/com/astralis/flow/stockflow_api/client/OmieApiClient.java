package com.astralis.flow.stockflow_api.client;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.astralis.flow.stockflow_api.config.OmieApiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class OmieApiClient {

  private static final Logger logger = LoggerFactory.getLogger(OmieApiClient.class);
  private static final String OMIE_ENDPOINT = "/produtos/pedido/";
  // Código Omie para "sem registros na página" — não é erro técnico
  private static final String OMIE_NO_RECORDS_FAULT = "SOAP-ENV:Client-5113";
  // Código Omie para rate limit — chamada redundante, aguardar antes de repetir
  private static final String OMIE_RATE_LIMIT_FAULT = "SOAP-ENV:Client-6";

  private final WebClient webClient;
  private final OmieApiConfig omieApiConfig;
  private final ObjectMapper objectMapper;

  public OmieApiClient(WebClient omieApiWebClient, OmieApiConfig omieApiConfig, ObjectMapper objectMapper) {
    this.webClient = omieApiWebClient;
    this.omieApiConfig = omieApiConfig;
    this.objectMapper = objectMapper;
  }

  /**
   * Executa uma chamada para a API Omie.
   *
   * @param call  o nome do método da API Omie (ex: "ListarPedidos")
   * @param param lista de parâmetros do método (conforme documentação Omie)
   * @return resposta JSON como String
   */
  public String call(String call, Map<String, Object> param) {
    return call(call, List.of(param));
  }

  public String call(String call, List<Map<String, Object>> param) {
    logger.info("Chamando API Omie. Call: {}", call);

    Map<String, Object> requestBody = Map.of(
        "app_key", omieApiConfig.getAppKey(),
        "app_secret", omieApiConfig.getAppSecret(),
        "call", call,
        "param", param);

    try {
      String response = webClient
          .post()
          .uri(OMIE_ENDPOINT)
          .bodyValue(requestBody)
          .retrieve()
          .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
              .flatMap(errorBody -> {
                // A Omie retorna HTTP 500 para erros de negócio — verifica o tipo
                if (isNoRecordsError(errorBody)) {
                  logger.info("API Omie retornou sem registros para call={}", call);
                  return Mono.error(new OmieNoRecordsException());
                }
                if (isRateLimitError(errorBody)) {
                  String waitMessage = extractFaultString(errorBody);
                  logger.warn("API Omie bloqueou por consumo redundante. Call={}: {}", call, waitMessage);
                  return Mono.error(new OmieRateLimitException(waitMessage));
                }
                logger.error("Erro HTTP {} ao chamar Omie call={}: {}",
                    clientResponse.statusCode(), call, errorBody);
                return Mono.error(new RuntimeException(
                    "Erro HTTP " + clientResponse.statusCode() + ": " + errorBody));
              }))
          .bodyToMono(String.class)
          .block();

      logger.info("Resposta da API Omie recebida. Call: {}, tamanho: {} chars",
          call, response != null ? response.length() : 0);
      return response;

    } catch (OmieNoRecordsException e) {
      return null;
    } catch (OmieRateLimitException e) {
      throw new RuntimeException("Omie bloqueou a requisição por consumo redundante: " + e.getMessage(), e);
    } catch (WebClientResponseException e) {
      logger.error("WebClientResponseException ao chamar Omie call={}: Status={}, Body={}",
          call, e.getStatusCode(), e.getResponseBodyAsString());
      throw new RuntimeException("Erro ao chamar API Omie [" + call + "]: " + e.getMessage(), e);
    } catch (Exception e) {
      logger.error("Erro inesperado ao chamar Omie call={}: {}", call, e.getMessage(), e);
      throw new RuntimeException("Erro inesperado ao chamar API Omie [" + call + "]", e);
    }
  }

  private boolean isNoRecordsError(String errorBody) {
    return hasFaultCode(errorBody, OMIE_NO_RECORDS_FAULT);
  }

  private boolean isRateLimitError(String errorBody) {
    return hasFaultCode(errorBody, OMIE_RATE_LIMIT_FAULT);
  }

  private boolean hasFaultCode(String errorBody, String expectedCode) {
    try {
      JsonNode node = objectMapper.readTree(errorBody);
      JsonNode faultcode = node.get("faultcode");
      return faultcode != null && expectedCode.equals(faultcode.asText());
    } catch (Exception e) {
      return false;
    }
  }

  private String extractFaultString(String errorBody) {
    try {
      JsonNode node = objectMapper.readTree(errorBody);
      JsonNode faultstring = node.get("faultstring");
      return faultstring != null ? faultstring.asText() : errorBody;
    } catch (Exception e) {
      return errorBody;
    }
  }

  private static class OmieNoRecordsException extends RuntimeException {
  }

  private static class OmieRateLimitException extends RuntimeException {
    public OmieRateLimitException(String message) {
      super(message);
    }
  }
}
