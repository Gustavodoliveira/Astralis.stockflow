package com.astralis.flow.stockflow_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;
import com.astralis.flow.stockflow_api.model.enums.ItemType;

public interface OrderProductionItemsRepository extends JpaRepository<OrderProductionItems, UUID> {

  List<OrderProductionItems> findByOrderProductionIdAndItemType(UUID orderProductionId, ItemType itemType);

  List<OrderProductionItems> findByOrderProductionId(UUID orderProductionId);

}
