ALTER TABLE external_order_item
    ADD COLUMN IF NOT EXISTS localizacao VARCHAR(255),
    ADD COLUMN IF NOT EXISTS nome_produto VARCHAR(255);
