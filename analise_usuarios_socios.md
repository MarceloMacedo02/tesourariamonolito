# Análise de Usuários e Sócios do Sistema Tesouraria

## Visão Geral

O sistema possui:
- 47 usuários cadastrados
- 60+ sócios cadastrados
- 3 perfis de acesso: ADMIN (1), TESOUREIRO (2), SOCIO (3)

## Estrutura

### Tabelas envolvidas:
1. **socios** - Cadastro completo dos sócios
2. **usuarios** - Credenciais de acesso ao sistema
3. **usuario_roles** - Associação de usuários aos perfis

### Relacionamentos:
- Cada usuário pode estar associado a um sócio (opcional)
- Cada sócio pode ter um usuário associado (opcional)
- Um usuário pode ter múltiplos perfis
- Perfis disponíveis: ROLE_ADMIN, ROLE_TESOUREIRO, ROLE_SOCIO

## Dados Relevantes

### Usuários com acesso:
- ADMIN: admin@sigest.com
- TESOUREIRO: tesoureiro
- Sócios com acesso individual (45 usuários):
  - marcelo.macedo
  - curumiemanuel@hotmail.com
  - marcelofilgueiras9@gmail.com
  - macielmarquesjose@outlook.com
  - cabalafdias@hotmail.com
  - carvalhogomesrachel@yahoo.com.br
  - rachelmddias@gmail.com
  - sergepittet12@hotmail.com
  - b1b29225-0b5e-4b6d-ab40-fc12f684bc54
  - servioalan@gmail.com
  - alexandre.kemenes@gmail.com
  - 5d24da77-5c5c-4419-a18b-da14786962cc
  - grazielamrfilgueiras@hotmail.com
  - joseleon.vasconcelos@gmail.com
  - marinalima101@hotmail.com
  - melzinha.leon@gmail.com
  - sanmadytk@gmail.com
  - bee560ef-2031-4b76-a286-b9fb72aeba15
  - e5aca530-f697-42e1-8044-cc9214473ae6
  - vanessajafraroyal@gmail.com
  - abraobruno76@gmail.com
  - portoaguiaralanjones@gmail.com
  - anamariagomeesmaciel@gmail.com
  - 473b1e93-8397-4e1e-8815-3ab0f4ac3585
  - angelabomfim16@gmail.com
  - 249a79ee-d578-4107-b3c5-1eaa8b602fbc
  - 76639b76-4e14-4665-8f3e-57ee1c1ae53f
  - oliveirawellington241@gmail.com
  - hulda_mara@hotmail.com
  - 2c9758e8-1bc4-46e0-8141-a002ac0dee6f
  - joao_victor_gomes@outlook.com
  - lahramiranda@hotmail.com
  - feliz.laura17@gmail.com
  - bd57ffa6-6e1a-4dd6-be6e-900baf3baf5c
  - liarakelmed2018@gmail.com
  - 607b69ea-d1c3-4fad-976f-7cf7903154c8
  - d3d83736-95de-41b3-9e0a-de863a14325e
  - tainarappgouveia@gmail.com
  - neylonaraujosilva@gmail.com
  - null (usuário inválido)
  - 96491565-8dcb-4210-95e6-78ab7a9c0c01
  - romulopaulocordao@gmail.com
  - thompsom2016@gmail.com
  - wrcaliman@gmail.com

### Sócios sem usuário associado:
- ALAN JONES PORTO AGUIAR (AFASTADO)
- DAVI GOMES MACÊDO (AFASTADO)
- HULDA MARA LUSTOSA PEREIRA DE ARAÚJO (AFASTADO)
- MARIA CELINA PEREIRA PESSOA (AFASTADO)
- NEYLON ARAUJO SILVA (AFASTADO)
- LARA CRUZ MIRANDA DA SILVA (AFASTADO)
- ABRAAO BRUNO DE OLIVEIRA MOTA (AFASTADO)
- INGRID MATOS ALMEIDA (FREQUENTE)
- PLAUGUS D´ITALO DE SOUSA ALMEIDA (FREQUENTE)
- Francisco José de Brito Junior (FREQUENTE)
- U D V NUCLEO FORTALEZA (FREQUENTE)
- CASA SALES PHB (FREQUENTE)

## Observações

1. Alguns usuários estão com usernames inválidos (UUIDs ou null)
2. Existem sócios AFASTADOS que ainda possuem usuários ativos
3. Alguns sócios FREQUENTES não possuem usuários associados
4. Os perfis estão corretamente atribuídos (ADMIN e TESOUREIRO possuem ambos os perfis, sócios possuem apenas o perfil de SOCIO)