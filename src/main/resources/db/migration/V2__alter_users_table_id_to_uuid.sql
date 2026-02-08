-- Habilitar a extensão uuid-ossp se não estiver habilitada
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Remover registros existentes (CUIDADO: isso apagará dados existentes)
-- Em produção, você deve migrar os dados primeiro
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- Alterar o tipo de coluna id para UUID
ALTER TABLE users 
    ALTER COLUMN id DROP DEFAULT,
    ALTER COLUMN id TYPE UUID USING uuid_generate_v4(),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4();

-- Remover a sequência que foi criada pelo BIGSERIAL
DROP SEQUENCE IF EXISTS users_id_seq;