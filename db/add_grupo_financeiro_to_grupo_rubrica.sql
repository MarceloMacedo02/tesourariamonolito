-- Script para adicionar a coluna grupo_financeiro_id na tabela grupo_rubrica

-- Adiciona a coluna grupo_financeiro_id
ALTER TABLE grupo_rubrica ADD COLUMN IF NOT EXISTS grupo_financeiro_id BIGINT;

-- Adiciona a chave estrangeira
DO $$
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
END $$;

-- Atualiza os registros existentes com um valor padrão (você pode ajustar conforme necessário)
-- Esta parte deve ser executada apenas se houver registros sem grupo_financeiro_id
UPDATE grupo_rubrica 
SET grupo_financeiro_id = (
    SELECT id FROM grupos_financeiros LIMIT 1
)
WHERE grupo_financeiro_id IS NULL;

-- Torna a coluna NOT NULL após atualizar os registros
ALTER TABLE grupo_rubrica ALTER COLUMN grupo_financeiro_id SET NOT NULL;