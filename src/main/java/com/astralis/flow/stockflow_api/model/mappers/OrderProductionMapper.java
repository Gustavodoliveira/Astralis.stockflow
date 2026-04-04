package com.astralis.flow.stockflow_api.model.mappers;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;

@Component
public class OrderProductionMapper {

  public OrderProductionResponse toResponse(OrderProduction order) {
    return new OrderProductionResponse(
        order.getId(),
        order.getUser().getId(),
        order.getUser().getName(),
        order.getProductionStatus(),
        order.getCreatedAt(),
        order.getUpdatedAt());
  }
}
