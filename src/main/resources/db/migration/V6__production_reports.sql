CREATE SEQUENCE IF NOT EXISTS production_order_number_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE order_production
  ADD COLUMN IF NOT EXISTS order_number BIGINT,
  ADD COLUMN IF NOT EXISTS requisition_number VARCHAR(60),
  ADD COLUMN IF NOT EXISTS description VARCHAR(255),
  ADD COLUMN IF NOT EXISTS started_on DATE,
  ADD COLUMN IF NOT EXISTS completed_on DATE,
  ADD COLUMN IF NOT EXISTS report_responsible VARCHAR(150);

UPDATE order_production
SET order_number = nextval('production_order_number_seq')
WHERE order_number IS NULL;

ALTER TABLE order_production
  ALTER COLUMN order_number SET DEFAULT nextval('production_order_number_seq'),
  ALTER COLUMN order_number SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_order_production_order_number ON order_production(order_number);

CREATE TABLE IF NOT EXISTS production_quality_check (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES order_production(id) ON DELETE CASCADE,
  item_id UUID NOT NULL REFERENCES order_production_items(id) ON DELETE CASCADE,
  aspect_approved BOOLEAN,
  color_approved BOOLEAN,
  odor_approved BOOLEAN,
  samples_collected BOOLEAN,
  labels_verified BOOLEAN,
  responsible VARCHAR(150),
  corrective_action TEXT,
  CONSTRAINT ux_production_quality_check_order_item UNIQUE (order_id, item_id)
);
