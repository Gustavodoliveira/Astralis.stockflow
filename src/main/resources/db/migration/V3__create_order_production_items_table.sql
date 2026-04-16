CREATE TABLE IF NOT EXISTS order_production_items (
    id                  UUID           PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            UUID           NOT NULL,
    item_type           VARCHAR(10)    NOT NULL CHECK (item_type IN ('INPUT', 'OUTPUT')),
    external_product_id VARCHAR(80)    NOT NULL,
    product_name        VARCHAR(120)   NOT NULL,
    unit                VARCHAR(20)    NOT NULL,
    quantity            NUMERIC(12, 3) NOT NULL,
    unit_weight         NUMERIC(12, 3) NOT NULL,
    lot                 VARCHAR(100)   NOT NULL,
    date_fabrication    DATE           NOT NULL,
    date_validity       DATE           NOT NULL,
    external_lot_id     INTEGER,
    FOREIGN KEY (order_id) REFERENCES order_production(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id            ON order_production_items (order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_external_product_id ON order_production_items (external_product_id);
CREATE INDEX IF NOT EXISTS idx_order_items_item_type           ON order_production_items (item_type);
