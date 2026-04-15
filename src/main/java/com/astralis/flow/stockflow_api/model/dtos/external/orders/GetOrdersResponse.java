package com.astralis.flow.stockflow_api.model.dtos.external.orders;

import java.util.List;

public record GetOrdersResponse(
    Boolean success,
    String page,
    Integer total,
    List<ExternalOrderDTO> response) {
}
