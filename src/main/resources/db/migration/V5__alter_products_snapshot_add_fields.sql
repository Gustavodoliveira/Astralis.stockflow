ALTER TABLE products_snapshot
  ADD COLUMN identificacao VARCHAR(40),
  ADD COLUMN descricao VARCHAR(255),
  ADD COLUMN unidade_medida VARCHAR(10),
  ADD COLUMN origem VARCHAR(30),
  ADD COLUMN peso_bruto NUMERIC(12,3),
  ADD COLUMN peso_liquido NUMERIC(12,3),
  ADD COLUMN ncm VARCHAR(20),
  ADD COLUMN ean VARCHAR(30),
  ADD COLUMN tipo VARCHAR(60),
  ADD COLUMN versao VARCHAR(20);

-- Índices úteis
CREATE INDEX IF NOT EXISTS idx_products_snapshot_identificacao
  ON products_snapshot(identificacao);

CREATE INDEX IF NOT EXISTS idx_products_snapshot_ncm
  ON products_snapshot(ncm);

CREATE INDEX IF NOT EXISTS idx_products_snapshot_ean
  ON products_snapshot(ean);