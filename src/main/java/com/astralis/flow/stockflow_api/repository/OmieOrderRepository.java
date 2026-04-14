package com.astralis.flow.stockflow_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.astralis.flow.stockflow_api.model.entities.OmieOrder;

public interface OmieOrderRepository extends JpaRepository<OmieOrder, UUID> {

  Optional<OmieOrder> findByCodigoPedido(Long codigoPedido);

  boolean existsByCodigoPedido(Long codigoPedido);
}
