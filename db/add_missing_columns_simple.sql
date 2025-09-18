-- Script simplificado para adicionar apenas as colunas à tabela existente
-- Assumindo que a tabela já existe e tem uma estrutura básica

-- Adiciona colunas que possivelmente estão faltando
ALTER TABLE reconciliacao_bancaria ADD COLUMN IF NOT EXISTS reconciliacao_mensal_id BIGINT;
ALTER TABLE reconciliacao_bancaria ADD COLUMN IF NOT EXISTS conta_financeira_id BIGINT;
ALTER TABLE reconciliacao_bancaria ADD COLUMN IF NOT EXISTS mes INTEGER;
ALTER TABLE reconciliacao_bancaria ADD COLUMN IF NOT EXISTS ano INTEGER;
ALTER TABLE reconciliacao_bancaria ADD COLUMN IF NOT EXISTS saldo_anterior DECIMAL(19,2);
ALTER TABLE reconciliacao_bancaria ADD COLUMN IF NOT EXISTS saldo_atual DECIMAL(19,2);

-- Adiciona índices se não existirem
CREATE INDEX IF NOT EXISTS idx_reconciliacao_bancaria_mensal_id ON reconciliacao_bancaria(reconciliacao_mensal_id);
CREATE INDEX IF NOT EXISTS idx_reconciliacao_bancaria_conta_id ON reconciliacao_bancaria(conta_financeira_id);

-- Adiciona chaves estrangeiras se não existirem
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_reconciliacao_bancaria_mensal' 
        AND table_name = 'reconciliacao_bancaria'
    ) THEN
        ALTER TABLE reconciliacao_bancaria 
        ADD CONSTRAINT fk_reconciliacao_bancaria_mensal 
        FOREIGN KEY (reconciliacao_mensal_id) REFERENCES reconciliacao_mensal(id);
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_reconciliacao_bancaria_conta' 
        AND table_name = 'reconciliacao_bancaria'
    ) THEN
        ALTER TABLE reconciliacao_bancaria 
        ADD CONSTRAINT fk_reconciliacao_bancaria_conta 
        FOREIGN KEY (conta_financeira_id) REFERENCES conta_financeira(id);
    END IF;
END $$;