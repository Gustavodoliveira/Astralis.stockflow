package com.astralis.flow.stockflow_api.model.mappers.external;

import com.astralis.flow.stockflow_api.model.dtos.external.products.GetProducts;
import com.astralis.flow.stockflow_api.model.dtos.external.products.ProductResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

  /**
   * Converte GetProducts (DTO da API externa) para ProductResponseDTO (DTO da
   * nossa API)
   */
  public ProductResponseDTO toResponseDTO(GetProducts product) {
    return new ProductResponseDTO(
        product.id(),
        product.identificacao(),
        product.descricao(),
        product.unidadeMedida(),
        product.tipo(),
        product.origem(),
        product.valorVenda(),
        product.valorCusto(),
        product.ncm(),
        product.status(),
        product.localizacao());
  }

  /**
   * Converte uma lista de GetProducts para lista de ProductResponseDTO
   */
  public List<ProductResponseDTO> toResponseDTOList(List<GetProducts> products) {
    return products.stream()
        .map(this::toResponseDTO)
        .toList();
  }
}