@echo off
echo Conectando ao banco de dados PostgreSQL e aplicando correções...

REM Cria um arquivo temporário com os comandos SQL
echo CREATE SEQUENCE IF NOT EXISTS movimentos_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1; > temp_fixes.sql
echo ALTER TABLE movimentos ALTER COLUMN id SET DEFAULT nextval('movimentos_id_seq'); >> temp_fixes.sql
echo ALTER SEQUENCE movimentos_id_seq OWNED BY movimentos.id; >> temp_fixes.sql
echo SELECT setval('movimentos_id_seq', COALESCE((SELECT MAX(id) FROM movimentos), 0) + 1); >> temp_fixes.sql
echo ALTER TABLE movimentos ALTER COLUMN grupo_financeiro_id DROP NOT NULL; >> temp_fixes.sql
echo UPDATE movimentos SET grupo_financeiro_id = (SELECT grupo_financeiro_id FROM grupo_rubrica WHERE grupo_rubrica.id = movimentos.grupo_rubrica_id) WHERE grupo_financeiro_id IS NULL AND grupo_rubrica_id IS NOT NULL; >> temp_fixes.sql
echo DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM movimentos WHERE grupo_financeiro_id IS NULL) THEN ALTER TABLE movimentos ALTER COLUMN grupo_financeiro_id SET NOT NULL; END IF; END $$; >> temp_fixes.sql
echo ALTER TABLE grupo_rubrica ADD COLUMN IF NOT EXISTS grupo_financeiro_id BIGINT; >> temp_fixes.sql
echo DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_grupo_rubrica_grupo_financeiro' AND table_name = 'grupo_rubrica') THEN ALTER TABLE grupo_rubrica ADD CONSTRAINT fk_grupo_rubrica_grupo_financeiro FOREIGN KEY (grupo_financeiro_id) REFERENCES grupos_financeiros(id); END IF; END $$; >> temp_fixes.sql
echo UPDATE grupo_rubrica SET grupo_financeiro_id = (SELECT id FROM grupos_financeiros LIMIT 1) WHERE grupo_financeiro_id IS NULL; >> temp_fixes.sql
echo DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM grupo_rubrica WHERE grupo_financeiro_id IS NULL) THEN ALTER TABLE grupo_rubrica ALTER COLUMN grupo_financeiro_id SET NOT NULL; END IF; END $$; >> temp_fixes.sql

REM Executa o script de correção completo
docker exec -i pg-tesouraria psql -U tesourario -d udv-tesouraria -f temp_fixes.sql

REM Remove o arquivo temporário
del temp_fixes.sql

echo Correções aplicadas com sucesso!