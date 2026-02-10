package com.astralis.flow.stockflow_api.model.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.astralis.flow.stockflow_api.model.enums.Kind;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "production_order_items", indexes = {
    @Index(name = "idx_poi_order", columnList = "production_order_id"),
    @Index(name = "idx_poi_ext_product", columnList = "external_product_id"),
    @Index(name = "idx_poi_kind", columnList = "kind"),
    @Index(name = "idx_poi_lot", columnList = "lot_code")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "production_order_id", nullable = false)
  private ProductionOrder productionOrder;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Kind kind;

  @Column(name = "external_product_id", nullable = false, length = 80)
  private String externalProductId;

  @Column(name = "product_name", nullable = false, length = 120)
  private String productName;

  @Column(nullable = false, precision = 12, scale = 3)
  private BigDecimal quantity = BigDecimal.ZERO;

  @Column(name = "lot_code", nullable = false, length = 60)
  private String lotCode;

  @Column(name = "manufactured_at")
  private LocalDate manufacturedAt;

  @Column(name = "expires_at")
  private LocalDate expiresAt;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}