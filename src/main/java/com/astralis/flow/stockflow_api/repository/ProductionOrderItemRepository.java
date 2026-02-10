package com.astralis.flow.stockflow_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astralis.flow.stockflow_api.model.entities.ProductionOrderItem;
import com.astralis.flow.stockflow_api.model.enums.Kind;

@Repository
public interface ProductionOrderItemRepository extends JpaRepository<ProductionOrderItem, Long> {
  List<ProductionOrderItem> findByProductionOrderId(Long productionOrderId);

  List<ProductionOrderItem> findByExternalProductId(String externalProductId);

  List<ProductionOrderItem> findByKind(Kind kind);

  List<ProductionOrderItem> findByLotCode(String lotCode);

  @Query("SELECT poi FROM ProductionOrderItem poi WHERE poi.productionOrder.id = :productionOrderId AND poi.kind = :kind")
  List<ProductionOrderItem> findByProductionOrderIdAndKind(@Param("productionOrderId") Long productionOrderId,
      @Param("kind") Kind kind);
}