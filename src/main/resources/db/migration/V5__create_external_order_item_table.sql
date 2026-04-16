CREATE TABLE IF NOT EXISTS external_order_item (
    id                UUID           PRIMARY KEY DEFAULT uuid_generate_v4(),
    external_order_id UUID           NOT NULL,
    external_item_id  BIGINT         NOT NULL,
    produto           BIGINT         NOT NULL,
    qtde              INTEGER        NOT NULL,
    valor_unitario    NUMERIC(12, 2) NOT NULL,
    dados_adicionais  VARCHAR(255),
    obs_item          VARCHAR(255),
    peso_bruto        DOUBLE PRECISION,
    peso_liquido      DOUBLE PRECISION,
    unidade           VARCHAR(20),
    lote              VARCHAR(100),
    data_validade     VARCHAR(30),
    localizacao       VARCHAR(255),
    nome_produto      VARCHAR(255),
    tipo_produto      VARCHAR(100),
    FOREIGN KEY (external_order_id) REFERENCES external_order(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_external_order_item_order_id ON external_order_item (external_order_id);
CREATE INDEX IF NOT EXISTS idx_external_order_item_produto  ON external_order_item (produto);
