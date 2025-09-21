#!/bin/bash

# Script para corrigir o banco de dados UDV Tesouraria

echo "Conectando ao banco de dados PostgreSQL e aplicando correções..."

# Executa o script de correção completo
docker exec -i postgres psql -U tesourario -d udv-tesouraria << EOF

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
-- Só aplicamos se não houver registros com grupo_financeiro_id NULL
DO \$\$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM movimentos WHERE grupo_financeiro_id IS NULL) THEN
        ALTER TABLE movimentos ALTER COLUMN grupo_financeiro_id SET NOT NULL;
    END IF;
END \$\$;

-- Script para adicionar a coluna grupo_financeiro_id na tabela grupo_rubrica

-- Adiciona a coluna grupo_financeiro_id
ALTER TABLE grupo_rubrica ADD COLUMN IF NOT EXISTS grupo_financeiro_id BIGINT;

-- Adiciona a chave estrangeira
DO \$\$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_grupo_rubrica_grupo_financeiro' 
        AND table_name = 'grupo_rubrica'
    ) THEN
        ALTER TABLE grupo_rubrica 
        ADD CONSTRAINT fk_grupo_rubrica_grupo_financeiro 
        FOREIGN KEY (grupo_financeiro_id) REFERENCES grupos_financeiros(id);
    END IF;
END \$\$;

-- Atualiza os registros existentes com um valor padrão (você pode ajustar conforme necessário)
-- Esta parte deve ser executada apenas se houver registros sem grupo_financeiro_id
UPDATE grupo_rubrica 
SET grupo_financeiro_id = (
    SELECT id FROM grupos_financeiros LIMIT 1
)
WHERE grupo_financeiro_id IS NULL;

-- Torna a coluna NOT NULL após atualizar os registros
-- Só aplicamos se não houver registros com grupo_financeiro_id NULL
DO \$\$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM grupo_rubrica WHERE grupo_financeiro_id IS NULL) THEN
        ALTER TABLE grupo_rubrica ALTER COLUMN grupo_financeiro_id SET NOT NULL;
    END IF;
END \$\$;

EOF

echo "Correções aplicadas com sucesso!"