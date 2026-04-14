CREATE TABLE IF NOT EXISTS omie_order_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    omie_order_id UUID NOT NULL,
    codigo_produto BIGINT NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    unidade VARCHAR(20) NOT NULL,
    valor_unitario NUMERIC(12,2) NOT NULL,
    valor_total NUMERIC(12,2) NOT NULL,
    peso_bruto DOUBLE PRECISION,
    peso_liquido DOUBLE PRECISION,
    numero_lote VARCHAR(100),
    data_fabricacao_lote VARCHAR(20),
    data_validade_lote VARCHAR(20),
    qtde_produto_lote INTEGER,
    codigo_agregacao_lote VARCHAR(100),
    FOREIGN KEY (omie_order_id) REFERENCES omie_order(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_omie_order_item_order_id ON omie_order_item (omie_order_id);
CREATE INDEX IF NOT EXISTS idx_omie_order_item_codigo_produto ON omie_order_item (codigo_produto);
CREATE INDEX IF NOT EXISTS idx_omie_order_item_numero_lote ON omie_order_item (numero_lote);
