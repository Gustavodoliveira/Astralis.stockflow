package com.astralis.flow.stockflow_api.model.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "external_order")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "external_id", nullable = false, unique = true)
  private Long externalId;

  @Column(name = "numero_pedido", nullable = false, length = 20)
  private String numeroPedido;

  @Column(name = "cliente", nullable = false)
  private Long cliente;

  @Column(name = "status", nullable = false, length = 50)
  private String status;

  @Column(name = "xped", length = 50)
  private String xped;

  @Column(name = "especie_volumes", length = 100)
  private String especieVolumes;

  @Column(name = "qtde_volumes", length = 20)
  private String qtdeVolumes;

  @Column(name = "data_previsao", length = 30)
  private String dataPrevisao;

  @Column(name = "data_criacao", length = 30)
  private String dataCriacao;

  @Column(name = "data_ultima_atualizacao", length = 30)
  private String dataUltimaAtualizacao;

  @Column(name = "valor_frete", precision = 12, scale = 2)
  private BigDecimal valorFrete;

  @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal valorTotal;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "status_interno", length = 50)
  private String statusInterno;

  @Column(name = "localizacao", length = 255)
  private String localizacao;

  @OneToMany(mappedBy = "externalOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<ExternalOrderItem> itens;
}
