-- Cria a tabela conta_financeira se não existir
CREATE TABLE IF NOT EXISTS conta_financeira (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    saldo_atual DECIMAL(19,2) NOT NULL
);

-- Cria a tabela reconciliacao_mensal se não existir
CREATE TABLE IF NOT EXISTS reconciliacao_mensal (
    id BIGSERIAL PRIMARY KEY,
    mes INTEGER NOT NULL,
    ano INTEGER NOT NULL,
    saldo_mes_anterior DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    resultado_operacional DECIMAL(19,2) NOT NULL DEFAULT 0.00
);

-- Adiciona índices se não existirem
CREATE INDEX IF NOT EXISTS idx_conta_financeira_nome ON conta_financeira(nome);