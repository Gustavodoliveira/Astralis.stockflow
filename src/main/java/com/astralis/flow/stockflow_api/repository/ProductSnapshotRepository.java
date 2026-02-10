package com.astralis.flow.stockflow_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astralis.flow.stockflow_api.model.entities.ProductSnapshot;

@Repository
public interface ProductSnapshotRepository extends JpaRepository<ProductSnapshot, Long> {
  Optional<ProductSnapshot> findByExternalProductId(String externalProductId);

  Boolean existsByExternalProductId(String externalProductId);
}