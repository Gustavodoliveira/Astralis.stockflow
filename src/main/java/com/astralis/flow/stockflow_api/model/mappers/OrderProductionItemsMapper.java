package com.astralis.flow.stockflow_api.model.mappers;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.order_production_items.CreateOrderProductionItemRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;

@Component
public class OrderProductionItemsMapper {

  public OrderProductionItems toEntity(CreateOrderProductionItemRequest dto, OrderProduction order) {
    OrderProductionItems item = new OrderProductionItems();
    item.setOrderProduction(order);
    item.setItemType(dto.itemType());
    item.setExternalProductId(dto.externalProductId());
    item.setProductName(dto.productName());
    item.setUnit(dto.unit());
    item.setQuantity(dto.quantity());
    item.setUnitWeight(dto.unitWeight());
    item.setLot(dto.lot());
    item.setExternalLotId(dto.externalLotId());
    item.setDateFabrication(dto.dateFabrication());
    item.setDateValidity(dto.dateValidity());
    return item;
  }

  public OrderProductionItemResponse toResponse(OrderProductionItems item) {
    return new OrderProductionItemResponse(
        item.getId(),
        item.getOrderProduction().getId(),
        item.getItemType(),
        item.getExternalProductId(),
        item.getProductName(),
        item.getUnit(),
        item.getQuantity(),
        item.getUnitWeight(),
        item.getLot(),
        item.getExternalLotId(),
        item.getDateFabrication(),
        item.getDateValidity());
  }
}
