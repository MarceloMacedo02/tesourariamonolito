-- Remove campos não utilizados da tabela reconciliacao_mensal
-- First, make the columns nullable if they exist
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'reconciliacao_mensal' AND column_name = 'total_entradas') THEN
        ALTER TABLE reconciliacao_mensal ALTER COLUMN total_entradas DROP NOT NULL;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'reconciliacao_mensal' AND column_name = 'total_saidas') THEN
        ALTER TABLE reconciliacao_mensal ALTER COLUMN total_saidas DROP NOT NULL;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'reconciliacao_mensal' AND column_name = 'saldo_sugerido') THEN
        ALTER TABLE reconciliacao_mensal ALTER COLUMN saldo_sugerido DROP NOT NULL;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'reconciliacao_mensal' AND column_name = 'saldo_final') THEN
        ALTER TABLE reconciliacao_mensal ALTER COLUMN saldo_final DROP NOT NULL;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'reconciliacao_mensal' AND column_name = 'data_reconciliacao') THEN
        ALTER TABLE reconciliacao_mensal ALTER COLUMN data_reconciliacao DROP NOT NULL;
    END IF;
END $$;

-- Then drop the columns if they exist
ALTER TABLE reconciliacao_mensal 
DROP COLUMN IF EXISTS total_entradas,
DROP COLUMN IF EXISTS total_saidas,
DROP COLUMN IF EXISTS saldo_sugerido,
DROP COLUMN IF EXISTS saldo_final,
DROP COLUMN IF EXISTS data_reconciliacao,
DROP COLUMN IF EXISTS observacoes;