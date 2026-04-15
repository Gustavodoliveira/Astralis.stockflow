ALTER TABLE external_order
    ADD COLUMN IF NOT EXISTS nome_cliente VARCHAR(255);
