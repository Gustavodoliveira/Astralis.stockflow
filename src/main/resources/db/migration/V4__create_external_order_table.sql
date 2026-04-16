CREATE TABLE IF NOT EXISTS external_order (
    id                      UUID           PRIMARY KEY DEFAULT uuid_generate_v4(),
    external_id             BIGINT         NOT NULL UNIQUE,
    numero_pedido           VARCHAR(20)    NOT NULL,
    cliente                 BIGINT         NOT NULL,
    status                  VARCHAR(50)    NOT NULL,
    xped                    VARCHAR(50),
    especie_volumes         VARCHAR(100),
    qtde_volumes            VARCHAR(20),
    data_previsao           VARCHAR(30),
    data_criacao            VARCHAR(30),
    data_ultima_atualizacao VARCHAR(30),
    valor_frete             NUMERIC(12, 2),
    valor_total             NUMERIC(12, 2) NOT NULL,
    user_id                 UUID,
    status_interno          VARCHAR(50),
    localizacao             VARCHAR(255),
    nome_cliente            VARCHAR(255),
    created_at              TIMESTAMPTZ    NOT NULL,
    updated_at              TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_external_order_external_id ON external_order (external_id);
CREATE INDEX IF NOT EXISTS idx_external_order_status      ON external_order (status);
CREATE INDEX IF NOT EXISTS idx_external_order_cliente     ON external_order (cliente);
