package com.astralis.flow.stockflow_api.model.dtos.order_production_items;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.astralis.flow.stockflow_api.model.enums.ItemType;

public record OrderProductionItemResponse(
    UUID id,
    UUID orderId,
    ItemType itemType,
    String externalProductId,
    String productName,
    String unit,
    BigDecimal quantity,
    BigDecimal unitWeight,
    String lot,
    Integer externalLotId,
    LocalDate dateFabrication,
    LocalDate dateValidity) {
}
