package com.astralis.flow.stockflow_api.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.astralis.flow.stockflow_api.model.enums.ItemType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "order_production_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductionItems {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderProduction orderProduction;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_type", nullable = false, length = 50)
  private ItemType itemType;

  @Column(name = "external_product_id", nullable = false, length = 100)
  private String externalProductId;

  @Column(name = "product_name", nullable = false, length = 100)
  private String productName;

  @Column(name = "unit", nullable = false, length = 20)
  private String unit;

  @Column(name = "quantity", nullable = false)
  private BigDecimal quantity;

  @Column(name = "unit_weight", nullable = false)
  private BigDecimal unitWeight;

  @Column(name = "lot", nullable = false, length = 50)
  private String lot;

  @Column(name = "external_lot_id")
  private Integer externalLotId;

  @Column(name = "date_fabrication", nullable = false)
  private LocalDate dateFabrication;

  @Column(name = "date_validity", nullable = false)
  private LocalDate dateValidity;
}
