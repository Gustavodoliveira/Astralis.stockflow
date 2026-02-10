CREATE TABLE production_orders (
  id BIGSERIAL PRIMARY KEY,
  order_number VARCHAR(40) NOT NULL UNIQUE,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);