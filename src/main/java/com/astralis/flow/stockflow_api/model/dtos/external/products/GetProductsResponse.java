package com.astralis.flow.stockflow_api.model.dtos.external.products;

import java.util.List;

public record GetProductsResponse(Boolean success,
    String page,
    Integer total,
    List<GetProducts> response) {

}
