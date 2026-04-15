package com.astralis.flow.stockflow_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astralis.flow.stockflow_api.model.entities.ExternalOrderItem;

@Repository
public interface ExternalOrderItemRepository extends JpaRepository<ExternalOrderItem, UUID> {
}
