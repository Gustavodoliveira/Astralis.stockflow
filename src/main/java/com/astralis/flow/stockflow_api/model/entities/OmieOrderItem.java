package com.astralis.flow.stockflow_api.model.entities;

import java.math.BigDecimal;
import java.util.UUID;

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
@Table(name = "omie_order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OmieOrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "omie_order_id", nullable = false)
  private OmieOrder omieOrder;

  @Column(name = "codigo_produto", nullable = false)
  private Long codigoProduto;

  @Column(name = "codigo", nullable = false, length = 50)
  private String codigo;

  @Column(name = "descricao", nullable = false, length = 255)
  private String descricao;

  @Column(name = "quantidade", nullable = false)
  private Integer quantidade;

  @Column(name = "unidade", nullable = false, length = 20)
  private String unidade;

  @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
  private BigDecimal valorUnitario;

  @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal valorTotal;

  @Column(name = "peso_bruto")
  private Double pesoBruto;

  @Column(name = "peso_liquido")
  private Double pesoLiquido;

  @Column(name = "numero_lote", length = 100)
  private String numeroLote;

  @Column(name = "data_fabricacao_lote", length = 20)
  private String dataFabricacaoLote;

  @Column(name = "data_validade_lote", length = 20)
  private String dataValidadeLote;

  @Column(name = "qtde_produto_lote")
  private Integer qtdeProdutoLote;

  @Column(name = "codigo_agregacao_lote", length = 100)
  private String codigoAgregacaoLote;
}
