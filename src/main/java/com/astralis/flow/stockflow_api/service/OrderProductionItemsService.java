package com.astralis.flow.stockflow_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.CreateOrderProductionItemRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.model.mappers.OrderProductionItemsMapper;
import com.astralis.flow.stockflow_api.repository.OrderProductionItemsRepository;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderProductionItemsService {

  private final OrderProductionRepository orderProductionRepository;

  private final OrderProductionItemsRepository orderProductionItemsRepository;

  private final OrderProductionItemsMapper orderProductionItemsMapper;

  // Create multiple items for an order production
  public List<OrderProductionItemResponse> createOrderProductionItems(List<CreateOrderProductionItemRequest> dtos) {
    UUID orderId = dtos.get(0).orderId();
    var order = orderProductionRepository.findById(orderId)
        .orElseThrow(() -> new OrderProductionNotExist("Order production with id '" + orderId + "' does not exist"));

    return dtos.stream()
        .map(dto -> orderProductionItemsRepository.save(orderProductionItemsMapper.toEntity(dto, order)))
        .map(orderProductionItemsMapper::toResponse)
        .toList();
  }

  // Get all items by order production id
  public List<OrderProductionItemResponse> getItemsByOrderProductionId(String orderId) {
    UUID uuid = UUID.fromString(orderId);
    var items = orderProductionItemsRepository.findByOrderProductionId(uuid);
    return items.stream()
        .map(orderProductionItemsMapper::toResponse)
        .toList();
  }

  // Get item by id
  public OrderProductionItemResponse getItemById(String id) {
    UUID uuid = UUID.fromString(id);
    var item = orderProductionItemsRepository.findById(uuid)
        .orElseThrow(() -> new OrderProductionNotExist("Order production item with id '" + id + "' does not exist"));
    return orderProductionItemsMapper.toResponse(item);
  }

  // Delete item by id
  public void deleteItemById(String id) {
    UUID uuid = UUID.fromString(id);
    if (!orderProductionItemsRepository.existsById(uuid)) {
      throw new OrderProductionNotExist("Order production item with id '" + id + "' does not exist");
    }
    orderProductionItemsRepository.deleteById(uuid);
  }

  // Update item by id
  public OrderProductionItemResponse updateOrderProductionItem(String id, CreateOrderProductionItemRequest dto) {
    UUID uuid = UUID.fromString(id);
    var existingItem = orderProductionItemsRepository.findById(uuid)
        .orElseThrow(() -> new OrderProductionNotExist("Order production item with id '" + id + "' does not exist"));

    var order = orderProductionRepository.findById(dto.orderId())
        .orElseThrow(
            () -> new OrderProductionNotExist("Order production with id '" + dto.orderId() + "' does not exist"));

    existingItem.setOrderProduction(order);
    existingItem.setItemType(dto.itemType());
    existingItem.setExternalProductId(dto.externalProductId());
    existingItem.setProductName(dto.productName());
    existingItem.setUnit(dto.unit());
    existingItem.setQuantity(dto.quantity());
    existingItem.setUnitWeight(dto.unitWeight());
    existingItem.setLot(dto.lot());
    existingItem.setDateFabrication(dto.dateFabrication());
    existingItem.setDateValidity(dto.dateValidity());

    orderProductionItemsRepository.save(existingItem);
    return orderProductionItemsMapper.toResponse(existingItem);

  }

}
