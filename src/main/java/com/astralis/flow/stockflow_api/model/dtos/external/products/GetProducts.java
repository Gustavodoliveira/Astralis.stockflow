package com.astralis.flow.stockflow_api.model.dtos.external.products;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetProducts(
    Long id,
    String identificacao,
    String descricao,

    @JsonProperty("unidade_medida") String unidadeMedida,

    String ean,
    String tipo,
    String origem,

    @JsonProperty("valor_venda") BigDecimal valorVenda,

    @JsonProperty("valor_custo") BigDecimal valorCusto,

    String ncm,
    String status,
    String localizacao,

    @JsonProperty("data_ultima_atualizacao") String dataUltimaAtualizacao,

    // Campos numéricos (podem ser 0 ou null)
    BigDecimal altura,

    @JsonProperty("peso_bruto") BigDecimal pesoBruto,

    @JsonProperty("peso_liquido") BigDecimal pesoLiquido,

    BigDecimal comprimento,
    BigDecimal largura,
    BigDecimal area,
    BigDecimal diametro,

    @JsonProperty("qtde_volume") BigDecimal qtdeVolume,

    @JsonProperty("qtde_embalagem") BigDecimal qtdeEmbalagem,

    @JsonProperty("lucro_pretendido") BigDecimal lucroPretendido,

    BigDecimal vcc,

    @JsonProperty("validade_vcc") BigDecimal validadeVcc,

    @JsonProperty("lote_minimo_compra") BigDecimal loteMinimo,

    @JsonProperty("maximo_empilhamentos") BigDecimal maximoEmpilhamentos,

    @JsonProperty("qtde_seguranca") BigDecimal qtdeSeguranca,

    @JsonProperty("qtde_minima") BigDecimal qtdeMinima,

    @JsonProperty("peso_tara") BigDecimal pesoTara,

    // Campos que podem ser null
    String fabricante,
    String projeto,
    String versao,
    String grupo,
    String subgrupo,

    @JsonProperty("tag_grupo") String tagGrupo,

    String linha,

    @JsonProperty("codigo_dun") String codigoDun,

    String genero,
    String cest,

    @JsonProperty("tipo_volume") String tipoVolume,

    @JsonProperty("tipo_embalagem") String tipoEmbalagem) {

}
