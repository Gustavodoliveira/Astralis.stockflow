package com.astralis.flow.stockflow_api.service;

import java.util.UUID;

import org.springframework.boot.webflux.autoconfigure.WebFluxProperties.Apiversion.Use;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.exception.UserNotExistException;
import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.entities.User;
import com.astralis.flow.stockflow_api.model.mappers.OrderProductionMapper;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderProductionService {

  private final OrderProductionRepository orderProductionRepository;

  private final UserRepository userRepository;

  private final OrderProductionMapper orderProductionMapper;

  public OrderProductionResponse createOrderProduction(CreateOrderProductionRequest orderProd) {
    User user = userRepository.findById(orderProd.userId())
        .orElseThrow(() -> new UserNotExistException(orderProd.userId().toString()));
    var orderProduction = orderProductionRepository.save(orderProductionMapper.toEntity(orderProd, user));
    return orderProductionMapper.toResponse(orderProduction);
  }

  public OrderProductionResponse getOrderProductionById(String id) {
    UUID uuid = UUID.fromString(id);
    var orderProduction = orderProductionRepository.findById(uuid)
        .orElseThrow(() -> new OrderProductionNotExist("Order production with id '" + id + "' does not exist"));
    return orderProductionMapper.toResponse(orderProduction);
  }

  public void deleteOrderProductionById(String id) {
    UUID uuid = UUID.fromString(id);
    if (!orderProductionRepository.existsById(uuid)) {
      throw new OrderProductionNotExist("Order production with id '" + id + "' does not exist");
    }
    orderProductionRepository.deleteById(uuid);
  }

  public OrderProductionResponse updateOrderProduction(String id, CreateOrderProductionRequest orderProd) {
    UUID uuid = UUID.fromString(id);
    var existingOrderProduction = orderProductionRepository.findById(uuid)
        .orElseThrow(() -> new OrderProductionNotExist("Order production with id '" + id + "' does not exist"));
    User user = userRepository.findById(orderProd.userId())
        .orElseThrow(() -> new UserNotExistException(orderProd.userId().toString()));
    existingOrderProduction.setUser(user);
    existingOrderProduction.setProductionStatus(orderProd.productionStatus());
    var updatedOrderProduction = orderProductionRepository.save(existingOrderProduction);
    return orderProductionMapper.toResponse(updatedOrderProduction);

  }
}