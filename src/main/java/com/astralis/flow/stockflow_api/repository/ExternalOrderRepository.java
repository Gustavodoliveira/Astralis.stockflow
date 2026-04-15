package com.astralis.flow.stockflow_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astralis.flow.stockflow_api.model.entities.ExternalOrder;

@Repository
public interface ExternalOrderRepository extends JpaRepository<ExternalOrder, UUID> {

  Optional<ExternalOrder> findByExternalId(Long externalId);

  boolean existsByExternalId(Long externalId);

  @Query("SELECT o FROM ExternalOrder o LEFT JOIN FETCH o.itens WHERE o.id = :id")
  Optional<ExternalOrder> findByIdWithItens(@Param("id") UUID id);
}
