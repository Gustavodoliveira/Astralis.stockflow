-- Limpar histórico Flyway para resolver checksum mismatch
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
-- A tabela users permanece, apenas zeramos o histórico