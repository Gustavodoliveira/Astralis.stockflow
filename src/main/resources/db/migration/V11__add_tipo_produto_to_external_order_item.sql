ALTER TABLE external_order_item
    ADD COLUMN IF NOT EXISTS tipo_produto VARCHAR(100);
