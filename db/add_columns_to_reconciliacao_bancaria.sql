-- Script para adicionar colunas à tabela reconciliacao_bancaria
-- Considerando que a tabela já existe com base no DDL fornecido

-- Adiciona as colunas necessárias
ALTER TABLE reconciliacao_bancaria 
ADD COLUMN IF NOT EXISTS reconciliacao_mensal_id BIGINT REFERENCES reconciliacao_mensal(id);

ALTER TABLE reconciliacao_bancaria 
ADD COLUMN IF NOT EXISTS conta_financeira_id BIGINT REFERENCES conta_financeira(id);

ALTER TABLE reconciliacao_bancaria 
ADD COLUMN IF NOT EXISTS mes INTEGER;

ALTER TABLE reconciliacao_bancaria 
ADD COLUMN IF NOT EXISTS ano INTEGER;

ALTER TABLE reconciliacao_bancaria 
ADD COLUMN IF NOT EXISTS saldo_anterior NUMERIC(38,2);

ALTER TABLE reconciliacao_bancaria 
ADD COLUMN IF NOT EXISTS saldo_atual NUMERIC(38,2);

-- Cria índices para melhorar performance
CREATE INDEX IF NOT EXISTS idx_reconciliacao_bancaria_mensal_id ON reconciliacao_bancaria(reconciliacao_mensal_id);
CREATE INDEX IF NOT EXISTS idx_reconciliacao_bancaria_conta_id ON reconciliacao_bancaria(conta_financeira_id);