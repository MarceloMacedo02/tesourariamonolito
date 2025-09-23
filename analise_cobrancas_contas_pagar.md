# Análise de Cobranças e Contas a Pagar do Sistema Tesouraria

## Visão Geral

O sistema possui:
- 103 cobranças registradas
- 0 contas a pagar registradas

## Estrutura

### Tabelas envolvidas:
1. **cobrancas** - Registro das cobranças (receitas e despesas)
2. **contas_pagar** - Registro das contas a pagar (despesas planejadas)

### Campos importantes em cobrancas:
- data_pagamento: Data de pagamento
- data_vencimento: Data de vencimento
- valor: Valor da cobrança
- descricao: Descrição da cobrança
- pagador: Nome do pagador (para cobranças de saída)
- status: Status da cobrança (ABERTA, PAGA, VENCIDA, CANCELADA, QUITADA)
- tipo_cobranca: Tipo de cobrança (MENSALIDADE, OUTRAS_RUBRICAS, AVULSA)
- tipo_movimento: Tipo de movimento (ENTRADA, SAIDA)
- ano_lancamento: Ano de lançamento
- mes_lancamento: Mês de lançamento

### Campos importantes em contas_pagar:
- data_pagamento: Data de pagamento
- data_vencimento: Data de vencimento
- valor: Valor da conta
- descricao: Descrição da conta
- status: Status da conta (ABERTA, PAGA, CANCELADA)

## Dados Relevantes

### Status das cobranças:
1. ABERTA: 64 cobranças
2. PAGA: 37 cobranças
3. VENCIDA: 0 cobranças
4. CANCELADA: 0 cobranças
5. QUITADA: 2 cobranças

### Tipos de cobrança:
1. MENSALIDADE: 26 cobranças
2. OUTRAS_RUBRICAS: 75 cobranças
3. AVULSA: 2 cobranças

### Tipos de movimento:
1. ENTRADA: 92 cobranças (receitas)
2. SAIDA: 11 cobranças (despesas)

### Principais rubricas nas cobranças:
1. Promoção Rifa Passeio
2. Mensalidade
3. Transf Pix enviada/recebida
4. Pgto QR Code Pix
5. Boleto pago
6. Tarifa de saque
7. Saque no débito
8. DIZIMO
9. JANTAR
10. COMPRA CAMISAS
11. REPASSE MESALIDADE
12. DOAÇÃO BILHETE

## Padrões Identificados

1. **Gestão de mensalidades:**
   - Sistema automatizado de geração de cobranças de mensalidades
   - Cobranças organizadas por grupos de mensalidade com valores diferentes
   - Controle de status (ABERTA, PAGA) para acompanhamento

2. **Outras rubricas:**
   - Diversos tipos de receitas e despesas são registrados como cobranças
   - Inclui transferências PIX, pagamentos de boletos, tarifas bancárias, etc.
   - Sistema diferencia entre receitas (ENTRADA) e despesas (SAIDA)

3. **Controle de datas:**
   - Registro de data de vencimento e data de pagamento
   - Informações de ano e mês de lançamento para facilitar conciliação

4. **Associações:**
   - Cobranças podem estar associadas a sócios, fornecedores, rubricas e transações
   - Isso permite rastrear a origem/destino dos valores

## Observações

1. Não há contas a pagar registradas, todas as despesas estão sendo registradas como cobranças
2. O sistema tem um controle robusto de mensalidades, com geração automática de cobranças
3. Existe uma diferenciação clara entre diferentes tipos de receitas e despesas
4. O status das cobranças permite um acompanhamento eficaz do fluxo de caixa
5. A ausência de registros em contas_pagar pode indicar que o fluxo de despesas é mais reativo (baseado em transações já ocorridas) do que proativo (baseado em compromissos futuros)