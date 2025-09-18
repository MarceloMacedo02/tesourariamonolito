-- Script principal para criar/atualizar a tabela reconciliacao_bancaria
-- Execute os scripts na seguinte ordem:

-- 1. Criar tabelas relacionadas (se não existirem)
\ir create_related_tables.sql

-- 2. Criar/atualizar a tabela reconciliacao_bancaria
\ir create_reconciliacao_bancaria_table.sql

-- 3. Alternativa: usar o script de alteração para adicionar colunas individualmente
-- \ir alter_reconciliacao_bancaria_table.sql

-- 4. Adicionar constraints NOT NULL após verificar dados
-- \ir add_not_null_constraints.sql