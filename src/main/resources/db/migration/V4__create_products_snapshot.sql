CREATE TABLE products_snapshot (
  id BIGSERIAL PRIMARY KEY,
  external_product_id VARCHAR(80) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);

CREATE INDEX idx_products_snapshot_external_id
  ON products_snapshot(external_product_id);