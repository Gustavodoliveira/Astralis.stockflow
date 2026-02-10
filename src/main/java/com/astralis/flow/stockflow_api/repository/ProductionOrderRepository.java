package com.astralis.flow.stockflow_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astralis.flow.stockflow_api.model.entities.ProductionOrder;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
  Optional<ProductionOrder> findByOrderNumber(String orderNumber);

  Boolean existsByOrderNumber(String orderNumber);
}