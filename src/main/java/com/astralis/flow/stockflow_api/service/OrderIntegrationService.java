package com.astralis.flow.stockflow_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.astralis.flow.stockflow_api.client.OmieApiClient;
import com.astralis.flow.stockflow_api.model.dtos.external.orders.OmieOrderDTO;
import com.astralis.flow.stockflow_api.model.mappers.OmieOrderMapper;
import com.astralis.flow.stockflow_api.repository.OmieOrderRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderIntegrationService {

  private final OmieApiClient omieApiClient;
  private final OmieOrderRepository omieOrderRepository;
  private final OmieOrderMapper omieOrderMapper;

  private static final Logger logger = LoggerFactory.getLogger(OrderIntegrationService.class);

  public List<OmieOrderDTO> integrateOrder() {
    String jsonResponse = omieApiClient.call("ListarPedidos", Map.of("etapa", "20"));
    logger.info("Resposta recebida da API externa para pedidos: {}", jsonResponse);
    return omieOrderMapper.fromJson(jsonResponse);
  }

}
