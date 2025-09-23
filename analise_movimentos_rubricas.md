# Análise de Movimentações e Rubricas do Sistema Tesouraria

## Visão Geral

O sistema possui:
- 249 movimentações financeiras registradas
- 34 rubricas cadastradas
- 14 grupos de rubrica
- 2 grupos financeiros (Receitas e Despesas)

## Estrutura

### Tabelas envolvidas:
1. **movimentos** - Registro das movimentações financeiras lançadas
2. **rubricas** - Tipos de receitas/despesas
3. **grupo_rubrica** - Grupos de rubricas (categorias)
4. **grupos_financeiros** - Grupos financeiros (Receitas/Despesas)

### Relacionamentos:
- Cada movimento está associado a uma rubrica
- Cada rubrica pertence a um grupo de rubrica
- Cada grupo de rubrica pertence a um grupo financeiro
- Cada movimento está associado a uma conta financeira
- Movimentos podem estar associados a sócios, fornecedores ou cobranças

## Dados Relevantes

### Grupos Financeiros:
1. Receitas
2. Despesas

### Grupos de Rubrica (principais):
1. TAXA DE ENCONTRO DOS PAIS
2. TARIFAS BANCÁRIAS - SAQUE
3. DESPESAS DE MANUTENÇÃO
4. IMPOSTO ISS
5. CONCESSIONÁRIA DE ENERGIA ELÉTRICA
6. CONSTRUÇÃO
7. BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR
8. MANUTENÇÃO UNIDADE
9. ORIENTAÇÃO ESPIRITUAL
10. RESERVA BILHETE
11. CAIXA ORGAN
12. DEPARTAMENTO DE PROMOÇÃO
13. REPASSE REGIONAL
14. REPASSE NACIONAL

### Rubricas mais significativas:
- MENSALIDADE (R$ 50,00)
- DOAÇÃO BILHETE DE VIAGEM MESTES VICENTE (R$ 218,00)
- PROMOÇÃO - RIFA PASSEIO
- PROMOÇÃO CAMISAS 7 DE SETEMBRO 2025
- PAGEMENTO ZELADOR (R$ 350,00)
- REPASSE NACIONAL (R$ 1250,00)

### Tipos de Movimentação:
- ENTRADA (Receitas)
- SAIDA (Despesas)

## Padrões Identificados

1. **Movimentações de Mensalidade:**
   - Para cada pagamento de mensalidade, são geradas múltiplas movimentações:
     - MENSALIDADE (valor principal)
     - TAXA REPASSE NACIONAL FP
     - TAXA REPASSE NACIONAL FS
     - TAXA REPASSE NACIONAL FA
     - TAXA REPASSE NACIONALFB
     - TAXA REPASSE NACIONAL CONTABILIDADE UNICA
     - TAXA BENEFICÊNCIA-REGIONAL
     - TAXA FUNDO DE RESERVA-REGIONAL
     - RECEBIMENTO FUNDO REGIONAL
     - RECEBIMENTO PLANTIO
     - ORIENTAÇÃO ESPIRITUAL (quando aplicável)
     - BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR (quando aplicável)
     - RECEBIMENTO/PAGAMENTO REPASSE REGIONAL (quando aplicável)

2. **Movimentações de Outras Rubricas:**
   - PROMOÇÃO - RIFA PASSEIO
   - DOAÇÃO BILHETE DE VIAGEM MESTES VICENTE
   - RECEBIMENTO DIZIMO E CAIXINHA DA BOA VONTADE
   - RECEBIMENTO DE JANTAR

3. **Movimentações de Despesas:**
   - DESPESAS DE MANUTENÇÃO
   - CONCESSIONÁRIA DE ENERGIA ELÉTRICA
   - DESPESAS DE CONSTRUÇÃO
   - TARIFAS BANCÁRIAS - SAQUE
   - PAGEMENTO ZELADOR

## Observações

1. O sistema utiliza um modelo complexo de rateio para mensalidades, distribuindo o valor entre diversas rubricas
2. Existem rubricas com valores padrão definidos e outras que variam conforme a movimentação
3. O sistema contempla tanto receitas quanto despesas de forma estruturada
4. Há controle de data/hora das movimentações e informações de origem/destino
5. As movimentações estão associadas a períodos (ano/mês de lançamento)