-- Script para inserir rubricas nos grupos de mensalidade
-- Para cada grupo de mensalidade, insere todas as rubricas do centro de custo 2

-- Primeiro, verificamos quais são as rubricas do centro de custo 2
-- SELECT id, nome, valor_padrao FROM rubricas WHERE centro_custo_id = 2;

-- Em seguida, inserimos essas rubricas em cada grupo de mensalidade

INSERT INTO grupo_mensalidade_rubrica (grupo_mensalidade_id, rubrica_id, valor)
SELECT 
    gm.id AS grupo_mensalidade_id,
    r.id AS rubrica_id,
    r.valor_padrao AS valor
FROM 
    grupo_mensalidade gm
CROSS JOIN 
    rubricas r
WHERE 
    r.centro_custo_id = 2;