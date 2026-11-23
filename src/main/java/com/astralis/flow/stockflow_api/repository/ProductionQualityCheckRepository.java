package com.astralis.flow.stockflow_api.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.astralis.flow.stockflow_api.model.entities.ProductionQualityCheck;

public interface ProductionQualityCheckRepository extends JpaRepository<ProductionQualityCheck, UUID> {
  List<ProductionQualityCheck> findByOrderId(UUID orderId);
  Optional<ProductionQualityCheck> findByOrderIdAndItemId(UUID orderId, UUID itemId);
}
