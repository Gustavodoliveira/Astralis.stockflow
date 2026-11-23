-- Hibernate EnumType.STRING persists Java enum names Input and Output.
-- V3 used uppercase-only values, preventing new production items from being stored.
ALTER TABLE order_production_items DROP CONSTRAINT IF EXISTS order_production_items_item_type_check;
ALTER TABLE order_production_items
  ADD CONSTRAINT order_production_items_item_type_check
  CHECK (item_type IN ('Input','Output','INPUT','OUTPUT'));
