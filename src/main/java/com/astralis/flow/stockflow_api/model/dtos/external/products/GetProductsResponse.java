package com.astralis.flow.stockflow_api.model.dtos.external.products;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetProductsResponse(
    Boolean success,
    String code,
    String message,
    String page,
    Integer total,
    List<GetProducts> response) {

}
