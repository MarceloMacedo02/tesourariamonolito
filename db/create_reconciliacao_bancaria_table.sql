-- Cria a tabela reconciliacao_bancaria se não existir
CREATE TABLE IF NOT EXISTS reconciliacao_bancaria (
    id BIGSERIAL PRIMARY KEY,
    reconciliacao_mensal_id BIGINT NOT NULL,
    conta_financeira_id BIGINT NOT NULL,
    mes INTEGER NOT NULL,
    ano INTEGER NOT NULL,
    saldo_anterior DECIMAL(19,2) NOT NULL,
    saldo_atual DECIMAL(19,2) NOT NULL
);

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