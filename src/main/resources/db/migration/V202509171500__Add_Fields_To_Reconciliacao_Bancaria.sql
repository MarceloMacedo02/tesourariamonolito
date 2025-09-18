-- Adiciona novos campos na tabela reconciliacao_bancaria
ALTER TABLE reconciliacao_bancaria 
ADD COLUMN mes INTEGER,
ADD COLUMN ano INTEGER,
ADD COLUMN saldo_anterior NUMERIC(38,2),
ADD COLUMN saldo_atual NUMERIC(38,2),
ADD COLUMN receitas NUMERIC(38,2),
ADD COLUMN despesas NUMERIC(38,2);