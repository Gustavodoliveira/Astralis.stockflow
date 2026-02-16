package com.astralis.flow.stockflow_api.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ExternalApiClient {

  private final WebClient webClient;

  @Autowired
  public ExternalApiClient(WebClient externalApiWebClient) {
    this.webClient = externalApiWebClient;
  }

  /**
   * Executa uma requisição GET para um endpoint específico
   * 
   * @param endpoint O endpoint da API (ex: "/products", "/users")
   * @return String com a resposta da API
   */
  public String get(String endpoint) {
    try {
      return webClient
          .get()
          .uri(endpoint)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(
                    new RuntimeException("Erro HTTP " + response.statusCode() + ": " + errorBody)));
          })
          .bodyToMono(String.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new RuntimeException("Erro ao fazer requisição GET para " + endpoint + ": " + e.getMessage(), e);
    }
  }

  /**
   * Executa uma requisição POST com payload JSON
   * 
   * @param endpoint O endpoint da API
   * @param body     O corpo da requisição (objeto que será serializado para JSON)
   * @return String com a resposta da API
   */
  public String post(String endpoint, Object body) {
    try {
      return webClient
          .post()
          .uri(endpoint)
          .bodyValue(body)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(
                    new RuntimeException("Erro HTTP " + response.statusCode() + ": " + errorBody)));
          })
          .bodyToMono(String.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new RuntimeException("Erro ao fazer requisição POST para " + endpoint + ": " + e.getMessage(), e);
    }
  }

  /**
   * Executa uma requisição PUT com payload JSON
   * 
   * @param endpoint O endpoint da API
   * @param body     O corpo da requisição
   * @return String com a resposta da API
   */
  public String put(String endpoint, Object body) {
    try {
      return webClient
          .put()
          .uri(endpoint)
          .bodyValue(body)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(
                    new RuntimeException("Erro HTTP " + response.statusCode() + ": " + errorBody)));
          })
          .bodyToMono(String.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new RuntimeException("Erro ao fazer requisição PUT para " + endpoint + ": " + e.getMessage(), e);
    }
  }

  /**
   * Executa uma requisição DELETE
   * 
   * @param endpoint O endpoint da API
   * @return String com a resposta da API
   */
  public String delete(String endpoint) {
    try {
      return webClient
          .delete()
          .uri(endpoint)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(
                    new RuntimeException("Erro HTTP " + response.statusCode() + ": " + errorBody)));
          })
          .bodyToMono(String.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new RuntimeException("Erro ao fazer requisição DELETE para " + endpoint + ": " + e.getMessage(), e);
    }
  }

  /**
   * Executa uma requisição GET com parâmetros de query
   * 
   * @param endpoint O endpoint da API
   * @param params   Mapa com os parâmetros de query
   * @return String com a resposta da API
   */
  public String getWithParams(String endpoint, Map<String, String> params) {
    try {
      WebClient.RequestHeadersUriSpec<?> uriSpec = webClient.get();
      WebClient.RequestHeadersSpec<?> headerSpec = uriSpec.uri(uriBuilder -> {
        uriBuilder.path(endpoint);
        if (params != null) {
          params.forEach(uriBuilder::queryParam);
        }
        return uriBuilder.build();
      });

      return headerSpec
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(
                    new RuntimeException("Erro HTTP " + response.statusCode() + ": " + errorBody)));
          })
          .bodyToMono(String.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new RuntimeException("Erro ao fazer requisição GET para " + endpoint + ": " + e.getMessage(), e);
    }
  }
}