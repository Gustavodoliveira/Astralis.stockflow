package com.astralis.flow.stockflow_api.model.mappers.external;

import com.astralis.flow.stockflow_api.model.dtos.external.client.ClientResponseDTO;
import com.astralis.flow.stockflow_api.model.dtos.external.client.GetClients;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMapper {

  public ClientResponseDTO toResponseDTO(GetClients client) {
    return new ClientResponseDTO(
        client.id(),
        client.identificacao(),
        client.razaoSocial(),
        client.nomeFantasia(),
        client.cnpj(),
        client.status());
  }

  public List<ClientResponseDTO> toResponseDTOList(List<GetClients> clients) {
    if (clients == null)
      return List.of();
    return clients.stream().map(this::toResponseDTO).toList();
  }
}
