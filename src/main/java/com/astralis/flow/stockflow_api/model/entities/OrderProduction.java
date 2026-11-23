package com.astralis.flow.stockflow_api.model.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_production")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduction {

  @Column(name = "order_number", insertable = false, updatable = false)
  private Long orderNumber;

  @Column(name = "requisition_number", length = 60)
  private String requisitionNumber;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "started_on")
  private LocalDate startedOn;

  @Column(name = "completed_on")
  private LocalDate completedOn;

  @Column(name = "report_responsible", length = 150)
  private String reportResponsible;

  // Preserve the original constructor used by existing services/tests.
  public OrderProduction(UUID id, User user, ProductionStatus productionStatus,
                         Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.user = user;
    this.productionStatus = productionStatus;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "production_status", nullable = false, length = 50)
  private ProductionStatus productionStatus;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}
