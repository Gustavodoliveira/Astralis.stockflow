package com.astralis.flow.stockflow_api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.model.dtos.order_production.ProductionQualityCheckRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.ProductionReportDetailsRequest;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;
import com.astralis.flow.stockflow_api.model.entities.ProductionQualityCheck;
import com.astralis.flow.stockflow_api.repository.OrderProductionItemsRepository;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.ProductionQualityCheckRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionReportManagementService {
  private final OrderProductionRepository orders;
  private final OrderProductionItemsRepository items;
  private final ProductionQualityCheckRepository checks;

  @Transactional
  public void updateDetails(UUID orderId, ProductionReportDetailsRequest data) {
    OrderProduction order = getOrder(orderId);
    order.setRequisitionNumber(data.requisitionNumber());
    order.setDescription(data.description());
    order.setStartedOn(data.startedOn());
    order.setCompletedOn(data.completedOn());
    order.setReportResponsible(data.responsible());
    orders.save(order);
  }

  @Transactional
  public void updateQuality(UUID orderId, UUID itemId, ProductionQualityCheckRequest data) {
    OrderProduction order = getOrder(orderId);
    OrderProductionItems item = items.findById(itemId)
        .filter(i -> i.getOrderProduction().getId().equals(orderId))
        .orElseThrow(() -> new IllegalArgumentException("Item is not part of the selected order"));

    ProductionQualityCheck check = checks.findByOrderIdAndItemId(orderId, itemId)
        .orElseGet(() -> {
          ProductionQualityCheck q = new ProductionQualityCheck();
          q.setId(UUID.randomUUID());
          q.setOrder(order);
          q.setItem(item);
          return q;
        });
    check.setAspectApproved(data.aspectApproved());
    check.setColorApproved(data.colorApproved());
    check.setOdorApproved(data.odorApproved());
    check.setSamplesCollected(data.samplesCollected());
    check.setLabelsVerified(data.labelsVerified());
    check.setResponsible(data.responsible());
    check.setCorrectiveAction(data.correctiveAction());
    checks.save(check);
  }

  private OrderProduction getOrder(UUID id) {
    return orders.findById(id).orElseThrow(
        () -> new OrderProductionNotExist("Order production with id '" + id + "' does not exist"));
  }
}
