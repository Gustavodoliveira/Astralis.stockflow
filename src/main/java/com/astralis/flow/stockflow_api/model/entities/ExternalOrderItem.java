package com.astralis.flow.stockflow_api.model.entities;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "external_order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalOrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "external_order_id", nullable = false)
  private ExternalOrder externalOrder;

  @Column(name = "external_item_id", nullable = false)
  private Long externalItemId;

  @Column(name = "produto", nullable = false)
  private Long produto;

  @Column(name = "qtde", nullable = false)
  private Integer qtde;

  @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
  private BigDecimal valorUnitario;

  @Column(name = "dados_adicionais", length = 255)
  private String dadosAdicionais;

  @Column(name = "obs_item", length = 255)
  private String obsItem;

  @Column(name = "peso_bruto")
  private Double pesoBruto;

  @Column(name = "peso_liquido")
  private Double pesoLiquido;

  @Column(name = "unidade", length = 20)
  private String unidade;

  @Column(name = "lote", length = 100)
  private String lote;

  @Column(name = "data_validade", length = 30)
  private String dataValidade;

  @Column(name = "localizacao", length = 255)
  private String localizacao;

  @Column(name = "nome_produto", length = 255)
  private String nomeProduto;

  @Column(name = "tipo_produto", length = 100)
  private String tipoProduto;
}
