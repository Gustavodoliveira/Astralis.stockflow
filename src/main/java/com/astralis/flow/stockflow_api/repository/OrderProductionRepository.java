package com.astralis.flow.stockflow_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.astralis.flow.stockflow_api.model.entities.OrderProduction;

public interface OrderProductionRepository extends JpaRepository<OrderProduction, UUID> {

  OrderProduction findByIdAndUserId(UUID id, UUID userId);
}
