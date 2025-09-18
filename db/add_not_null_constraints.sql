-- Script para verificar e adicionar constraints NOT NULL após a criação das colunas
-- Este script deve ser executado após garantir que não há valores NULL nas colunas

DO $$
BEGIN
    -- Verifica se todas as linhas da coluna reconciliacao_mensal_id são não nulas
    IF NOT EXISTS (
        SELECT 1 FROM reconciliacao_bancaria WHERE reconciliacao_mensal_id IS NULL
    ) THEN
        -- Adiciona constraint NOT NULL
        ALTER TABLE reconciliacao_bancaria 
        ALTER COLUMN reconciliacao_mensal_id SET NOT NULL;
    END IF;
    
    -- Verifica se todas as linhas da coluna conta_financeira_id são não nulas
    IF NOT EXISTS (
        SELECT 1 FROM reconciliacao_bancaria WHERE conta_financeira_id IS NULL
    ) THEN
        -- Adiciona constraint NOT NULL
        ALTER TABLE reconciliacao_bancaria 
        ALTER COLUMN conta_financeira_id SET NOT NULL;
    END IF;
    
    -- Verifica se todas as linhas da coluna mes são não nulas
    IF NOT EXISTS (
        SELECT 1 FROM reconciliacao_bancaria WHERE mes IS NULL
    ) THEN
        -- Adiciona constraint NOT NULL
        ALTER TABLE reconciliacao_bancaria 
        ALTER COLUMN mes SET NOT NULL;
    END IF;
    
    -- Verifica se todas as linhas da coluna ano são não nulas
    IF NOT EXISTS (
        SELECT 1 FROM reconciliacao_bancaria WHERE ano IS NULL
    ) THEN
        -- Adiciona constraint NOT NULL
        ALTER TABLE reconciliacao_bancaria 
        ALTER COLUMN ano SET NOT NULL;
    END IF;
    
    -- Verifica se todas as linhas da coluna saldo_anterior são não nulas
    IF NOT EXISTS (
        SELECT 1 FROM reconciliacao_bancaria WHERE saldo_anterior IS NULL
    ) THEN
        -- Adiciona constraint NOT NULL
        ALTER TABLE reconciliacao_bancaria 
        ALTER COLUMN saldo_anterior SET NOT NULL;
    END IF;
    
    -- Verifica se todas as linhas da coluna saldo_atual são não nulas
    IF NOT EXISTS (
        SELECT 1 FROM reconciliacao_bancaria WHERE saldo_atual IS NULL
    ) THEN
        -- Adiciona constraint NOT NULL
        ALTER TABLE reconciliacao_bancaria 
        ALTER COLUMN saldo_atual SET NOT NULL;
    END IF;
END $$;