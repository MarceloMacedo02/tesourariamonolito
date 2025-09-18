-- Adiciona colunas à tabela reconciliacao_bancaria se não existirem
-- Verifica se a tabela existe e tem as colunas necessárias
DO $
BEGIN
    -- Verifica se a coluna id existe
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'id'
    ) THEN
        -- Adiciona a coluna id
        ALTER TABLE reconciliacao_bancaria ADD COLUMN id BIGSERIAL;
        -- Adiciona a constraint de chave primária
        ALTER TABLE reconciliacao_bancaria ADD CONSTRAINT reconciliacao_bancaria_pkey PRIMARY KEY (id);
    END IF;
    
    -- Verifica e adiciona outras colunas se não existirem
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'reconciliacao_mensal_id'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN reconciliacao_mensal_id BIGINT;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'conta_financeira_id'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN conta_financeira_id BIGINT;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'mes'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN mes INTEGER;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'ano'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN ano INTEGER;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'saldo_anterior'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN saldo_anterior DECIMAL(19,2);
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reconciliacao_bancaria' AND column_name = 'saldo_atual'
    ) THEN
        ALTER TABLE reconciliacao_bancaria ADD COLUMN saldo_atual DECIMAL(19,2);
    END IF;
END $;

-- Adiciona constraints NOT NULL apenas se não houver dados na tabela ou se todas as linhas tiverem valores não nulos
-- Esta verificação deve ser feita manualmente após a execução do script

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