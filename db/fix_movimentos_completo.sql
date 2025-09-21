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

-- Corrige o problema do grupo_financeiro_id
-- Primeiro, verificamos se a coluna permite NULL
ALTER TABLE movimentos ALTER COLUMN grupo_financeiro_id DROP NOT NULL;

-- Depois, atualizamos os registros existentes que têm NULL em grupo_financeiro_id
-- com base no grupo_rubrica_id (se existir)
UPDATE movimentos 
SET grupo_financeiro_id = (
    SELECT grupo_financeiro_id 
    FROM grupo_rubrica 
    WHERE grupo_rubrica.id = movimentos.grupo_rubrica_id
)
WHERE grupo_financeiro_id IS NULL AND grupo_rubrica_id IS NOT NULL;

-- Finalmente, voltamos a restrição NOT NULL para a coluna
ALTER TABLE movimentos ALTER COLUMN grupo_financeiro_id SET NOT NULL;