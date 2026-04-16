package com.astralis.flow.stockflow_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.exception.UserNotExistException;
import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionWithItemsRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionWithItemsResponse;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;
import com.astralis.flow.stockflow_api.model.entities.User;
import com.astralis.flow.stockflow_api.model.mappers.OrderProductionItemsMapper;
import com.astralis.flow.stockflow_api.model.mappers.OrderProductionMapper;
import com.astralis.flow.stockflow_api.repository.OrderProductionItemsRepository;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderProductionService {

  private final OrderProductionRepository orderProductionRepository;

  private final UserRepository userRepository;

  private final OrderProductionMapper orderProductionMapper;

  private final OrderProductionItemsRepository orderProductionItemsRepository;

  private final OrderProductionItemsMapper orderProductionItemsMapper;

  public OrderProductionWithItemsResponse createOrderProductionWithItems(
      CreateOrderProductionWithItemsRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotExistException(request.userId().toString()));

    var orderProduction = orderProductionRepository.save(
        orderProductionMapper.toEntity(new CreateOrderProductionRequest(request.userId(), request.productionStatus()),
            user));

    List<OrderProductionItemResponse> itemResponses = request.items().stream()
        .map(payload -> {
          OrderProductionItems item = new OrderProductionItems();
          item.setOrderProduction(orderProduction);
          item.setItemType(payload.itemType());
          item.setExternalProductId(payload.externalProductId());
          item.setProductName(payload.productName());
          item.setUnit(payload.unit());
          item.setQuantity(payload.quantity());
          item.setUnitWeight(payload.unitWeight());
          item.setLot(payload.lot());
          item.setExternalLotId(payload.externalLotId());
          item.setDateFabrication(payload.dateFabrication());
          item.setDateValidity(payload.dateValidity());
          return orderProductionItemsMapper.toResponse(orderProductionItemsRepository.save(item));
        })
        .toList();

    return new OrderProductionWithItemsResponse(
        orderProduction.getId(),
        orderProduction.getUser().getId(),
        orderProduction.getUser().getName(),
        orderProduction.getProductionStatus(),
        orderProduction.getCreatedAt(),
        orderProduction.getUpdatedAt(),
        itemResponses);
  }

  public OrderProductionResponse createOrderProduction(CreateOrderProductionRequest orderProd) {
    User user = userRepository.findById(orderProd.userId())
        .orElseThrow(() -> new UserNotExistException(orderProd.userId().toString()));
    var orderProduction = orderProductionRepository.save(orderProductionMapper.toEntity(orderProd, user));
    return orderProductionMapper.toResponse(orderProduction);
  }

  public java.util.List<OrderProductionResponse> getAllOrderProductions() {
    var orderProductions = orderProductionRepository.findAll();
    return orderProductions.stream()
        .map(orderProductionMapper::toResponse)
        .toList();
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