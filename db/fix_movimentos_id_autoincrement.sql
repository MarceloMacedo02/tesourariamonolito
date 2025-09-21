-- Script para corrigir o problema de auto-incremento da coluna id na tabela movimentos

-- Verifica se a sequência existe, se não existir, cria
CREATE SEQUENCE IF NOT EXISTS movimentos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Associa a sequência à coluna id da tabela movimentos
ALTER TABLE movimentos ALTER COLUMN id SET DEFAULT nextval('movimentos_id_seq');

-- Garante que a sequência seja "owned by" a coluna id
ALTER SEQUENCE movimentos_id_seq OWNED BY movimentos.id;

-- Atualiza a sequência para começar após o maior ID existente (se houver registros)
SELECT setval('movimentos_id_seq', COALESCE((SELECT MAX(id) FROM movimentos), 0) + 1);