CREATE TABLE IF NOT EXISTS production_order_items (
  id BIGSERIAL PRIMARY KEY,
  production_order_id BIGINT NOT NULL,
  kind VARCHAR(10) NOT NULL, -- INPUT / OUTPUT
  external_product_id VARCHAR(80) NOT NULL,
  product_name VARCHAR(120) NOT NULL, -- snapshot do nome no momento da OP
  quantity NUMERIC(12,3) NOT NULL DEFAULT 0,
  lot_code VARCHAR(60) NOT NULL,
  manufactured_at DATE,
  expires_at DATE,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ,

  CONSTRAINT fk_poi_order
    FOREIGN KEY (production_order_id)
    REFERENCES production_orders(id)
    ON DELETE CASCADE,

  CONSTRAINT ck_poi_kind
    CHECK (kind IN ('INPUT','OUTPUT'))
);

CREATE INDEX IF NOT EXISTS idx_poi_order ON production_order_items(production_order_id);
CREATE INDEX IF NOT EXISTS idx_poi_ext_product ON production_order_items(external_product_id);
CREATE INDEX IF NOT EXISTS idx_poi_kind ON production_order_items(kind);
CREATE INDEX IF NOT EXISTS idx_poi_lot ON production_order_items(lot_code);