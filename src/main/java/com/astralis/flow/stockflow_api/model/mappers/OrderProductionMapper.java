package com.astralis.flow.stockflow_api.model.mappers;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.User;

@Component
public class OrderProductionMapper {

  public OrderProduction toEntity(CreateOrderProductionRequest dto, User user) {
    OrderProduction order = new OrderProduction();
    order.setUser(user);
    order.setProductionStatus(dto.productionStatus());
    return order;
  }

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
