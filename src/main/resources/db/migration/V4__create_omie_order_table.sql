CREATE TABLE IF NOT EXISTS omie_order (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    codigo_pedido BIGINT NOT NULL UNIQUE,
    numero_pedido VARCHAR(20) NOT NULL,
    codigo_cliente BIGINT NOT NULL,
    data_previsao VARCHAR(20),
    etapa VARCHAR(10) NOT NULL,
    quantidade_itens INTEGER NOT NULL,
    data_inclusao VARCHAR(20),
    data_alteracao VARCHAR(20),
    peso_bruto_total DOUBLE PRECISION,
    peso_liquido_total DOUBLE PRECISION,
    quantidade_volumes INTEGER,
    marca_volumes VARCHAR(100),
    especie_volumes VARCHAR(100),
    inicio_separacao TIMESTAMPTZ,
    termino_separacao TIMESTAMPTZ,
    separador VARCHAR(150),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_omie_order_codigo_pedido ON omie_order (codigo_pedido);
CREATE INDEX IF NOT EXISTS idx_omie_order_etapa ON omie_order (etapa);
