package com.astralis.flow.stockflow_api.model.entities;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products_snapshot", indexes = {
    @Index(name = "idx_products_snapshot_external_id", columnList = "external_product_id"),
    @Index(name = "idx_products_snapshot_identificacao", columnList = "identificacao"),
    @Index(name = "idx_products_snapshot_ncm", columnList = "ncm"),
    @Index(name = "idx_products_snapshot_ean", columnList = "ean")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSnapshot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "external_product_id", nullable = false, unique = true, length = 80)
  private String externalProductId;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(length = 40)
  private String identificacao;

  @Column(length = 255)
  private String descricao;

  @Column(name = "unidade_medida", length = 10)
  private String unidadeMedida;

  @Column(length = 30)
  private String origem;

  @Column(name = "peso_bruto", precision = 12, scale = 3)
  private BigDecimal pesoBruto;

  @Column(name = "peso_liquido", precision = 12, scale = 3)
  private BigDecimal pesoLiquido;

  @Column(length = 20)
  private String ncm;

  @Column(length = 30)
  private String ean;

  @Column(length = 60)
  private String tipo;

  @Column(length = 20)
  private String versao;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}