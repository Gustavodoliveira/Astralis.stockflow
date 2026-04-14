package com.astralis.flow.stockflow_api.model.entities;

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
@Table(name = "omie_order")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OmieOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "codigo_pedido", nullable = false, unique = true)
  private Long codigoPedido;

  @Column(name = "numero_pedido", nullable = false, length = 20)
  private String numeroPedido;

  @Column(name = "codigo_cliente", nullable = false)
  private Long codigoCliente;

  @Column(name = "data_previsao", length = 20)
  private String dataPrevisao;

  @Column(name = "etapa", nullable = false, length = 10)
  private String etapa;

  @Column(name = "quantidade_itens", nullable = false)
  private Integer quantidadeItens;

  @Column(name = "data_inclusao", length = 20)
  private String dataInclusao;

  @Column(name = "data_alteracao", length = 20)
  private String dataAlteracao;

  @Column(name = "peso_bruto_total")
  private Double pesoBrutoTotal;

  @Column(name = "peso_liquido_total")
  private Double pesoLiquidoTotal;

  @Column(name = "quantidade_volumes")
  private Integer quantidadeVolumes;

  @Column(name = "marca_volumes", length = 100)
  private String marcaVolumes;

  @Column(name = "especie_volumes", length = 100)
  private String especieVolumes;

  @Column(name = "inicio_separacao")
  private Instant inicioSeparacao;

  @Column(name = "termino_separacao")
  private Instant terminoSeparacao;

  @Column(name = "separador", length = 150)
  private String separador;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @OneToMany(mappedBy = "omieOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<OmieOrderItem> itens;
}
