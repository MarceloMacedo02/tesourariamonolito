-- Script para adicionar apenas as colunas necessárias à tabela reconciliacao_bancaria existente
-- Este script assume que a tabela já existe e pode ou não ter todas as colunas

-- Adiciona colunas que possivelmente estão faltando
DO $$
BEGIN
    -- Verifica e adiciona a coluna id se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'id'
    ) THEN
        -- Se não tem id, precisamos recriar a tabela ou adicionar com cuidado
        -- Esta abordagem é mais complexa, então vamos sugerir recriar a tabela
        RAISE NOTICE 'A coluna id não existe. Considere recriar a tabela.';
    END IF;
    
    -- Verifica e adiciona a coluna reconciliacao_mensal_id se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'reconciliacao_mensal_id'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN reconciliacao_mensal_id BIGINT;
    END IF;
    
    -- Verifica e adiciona a coluna conta_financeira_id se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'conta_financeira_id'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN conta_financeira_id BIGINT;
    END IF;
    
    -- Verifica e adiciona a coluna mes se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'mes'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN mes INTEGER;
    END IF;
    
    -- Verifica e adiciona a coluna ano se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'ano'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN ano INTEGER;
    END IF;
    
    -- Verifica e adiciona a coluna saldo_anterior se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'saldo_anterior'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN saldo_anterior DECIMAL(19,2);
    END IF;
    
    -- Verifica e adiciona a coluna saldo_atual se não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'saldo_atual'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN saldo_atual DECIMAL(19,2);
    END IF;
    
    -- Adiciona índices se não existirem
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE tablename = 'reconciliacao_bancaria' AND indexname = 'idx_reconciliacao_bancaria_mensal_id'
    ) THEN
        CREATE INDEX idx_reconciliacao_bancaria_mensal_id ON reconciliacao_bancaria(reconciliacao_mensal_id);
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE tablename = 'reconciliacao_bancaria' AND indexname = 'idx_reconciliacao_bancaria_conta_id'
    ) THEN
        CREATE INDEX idx_reconciliacao_bancaria_conta_id ON reconciliacao_bancaria(conta_financeira_id);
    END IF;
    
    -- Adiciona chaves estrangeiras se não existirem
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