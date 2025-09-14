--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Debian 17.5-1.pgdg120+1)
-- Dumped by pg_dump version 17.0

-- Started on 2025-09-12 14:30:41

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 16672)
-- Name: centro_custo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.centro_custo (
    ativo boolean NOT NULL,
    entradas double precision DEFAULT 0.0 NOT NULL,
    saidas double precision DEFAULT 0.0 NOT NULL,
    id bigint NOT NULL,
    nome character varying(255) NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 16671)
-- Name: centro_custo_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.centro_custo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3580 (class 0 OID 0)
-- Dependencies: 217
-- Name: centro_custo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.centro_custo_id_seq OWNED BY public.centro_custo.id;


--
-- TOC entry 220 (class 1259 OID 16683)
-- Name: cobrancas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cobrancas (
    data_pagamento date,
    data_vencimento date NOT NULL,
    valor real NOT NULL,
    fornecedor_id bigint,
    grupo_mensalidade_id bigint,
    id bigint NOT NULL,
    rubrica_id bigint,
    socio_id bigint,
    transacao_id bigint,
    descricao character varying(255),
    pagador character varying(255),
    status character varying(255) NOT NULL,
    tipo_cobranca character varying(255) NOT NULL,
    tipo_movimento character varying(255) NOT NULL,
    CONSTRAINT cobrancas_status_check CHECK (((status)::text = ANY ((ARRAY['ABERTA'::character varying, 'PAGA'::character varying, 'VENCIDA'::character varying, 'CANCELADA'::character varying, 'QUITADA'::character varying])::text[]))),
    CONSTRAINT cobrancas_tipo_cobranca_check CHECK (((tipo_cobranca)::text = ANY ((ARRAY['MENSALIDADE'::character varying, 'OUTRAS_RUBRICAS'::character varying, 'AVULSA'::character varying])::text[]))),
    CONSTRAINT cobrancas_tipo_movimento_check CHECK (((tipo_movimento)::text = ANY ((ARRAY['ENTRADA'::character varying, 'SAIDA'::character varying])::text[])))
);


--
-- TOC entry 219 (class 1259 OID 16682)
-- Name: cobrancas_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cobrancas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3581 (class 0 OID 0)
-- Dependencies: 219
-- Name: cobrancas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cobrancas_id_seq OWNED BY public.cobrancas.id;


--
-- TOC entry 222 (class 1259 OID 16695)
-- Name: conta_financeira; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.conta_financeira (
    saldo_atual real NOT NULL,
    id bigint NOT NULL,
    nome character varying(255) NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 16694)
-- Name: conta_financeira_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.conta_financeira_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3582 (class 0 OID 0)
-- Dependencies: 221
-- Name: conta_financeira_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.conta_financeira_id_seq OWNED BY public.conta_financeira.id;


--
-- TOC entry 224 (class 1259 OID 16704)
-- Name: contas_pagar; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contas_pagar (
    data_pagamento date,
    data_vencimento date NOT NULL,
    valor real NOT NULL,
    fornecedor_id bigint,
    id bigint NOT NULL,
    rubrica_id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT contas_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['ABERTA'::character varying, 'PAGA'::character varying, 'CANCELADA'::character varying])::text[])))
);


--
-- TOC entry 223 (class 1259 OID 16703)
-- Name: contas_pagar_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.contas_pagar_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3583 (class 0 OID 0)
-- Dependencies: 223
-- Name: contas_pagar_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.contas_pagar_id_seq OWNED BY public.contas_pagar.id;


--
-- TOC entry 225 (class 1259 OID 16713)
-- Name: fornecedor_enderecos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fornecedor_enderecos (
    fornecedor_id bigint NOT NULL,
    bairro character varying(255),
    cep character varying(255),
    cidade character varying(255),
    complemento character varying(255),
    estado character varying(255),
    logradouro character varying(255),
    numero character varying(255)
);


--
-- TOC entry 227 (class 1259 OID 16719)
-- Name: fornecedores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fornecedores (
    ativo boolean NOT NULL,
    data_cadastro date NOT NULL,
    id bigint NOT NULL,
    celular character varying(20),
    cnpj character varying(20) NOT NULL,
    telefone_comercial character varying(20),
    email character varying(100),
    nome character varying(255) NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 16718)
-- Name: fornecedores_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.fornecedores_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3584 (class 0 OID 0)
-- Dependencies: 226
-- Name: fornecedores_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.fornecedores_id_seq OWNED BY public.fornecedores.id;


--
-- TOC entry 229 (class 1259 OID 16728)
-- Name: grupo_mensalidade; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grupo_mensalidade (
    id bigint NOT NULL,
    nome character varying(255)
);


--
-- TOC entry 228 (class 1259 OID 16727)
-- Name: grupo_mensalidade_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grupo_mensalidade_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3585 (class 0 OID 0)
-- Dependencies: 228
-- Name: grupo_mensalidade_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grupo_mensalidade_id_seq OWNED BY public.grupo_mensalidade.id;


--
-- TOC entry 231 (class 1259 OID 16735)
-- Name: grupo_mensalidade_rubrica; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grupo_mensalidade_rubrica (
    valor real NOT NULL,
    grupo_mensalidade_id bigint NOT NULL,
    id bigint NOT NULL,
    rubrica_id bigint NOT NULL
);


--
-- TOC entry 230 (class 1259 OID 16734)
-- Name: grupo_mensalidade_rubrica_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grupo_mensalidade_rubrica_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3586 (class 0 OID 0)
-- Dependencies: 230
-- Name: grupo_mensalidade_rubrica_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grupo_mensalidade_rubrica_id_seq OWNED BY public.grupo_mensalidade_rubrica.id;


--
-- TOC entry 233 (class 1259 OID 16742)
-- Name: grupos_financeiros; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grupos_financeiros (
    ativo boolean NOT NULL,
    id bigint NOT NULL,
    descricao character varying(255),
    nome character varying(255) NOT NULL
);


--
-- TOC entry 232 (class 1259 OID 16741)
-- Name: grupos_financeiros_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grupos_financeiros_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3587 (class 0 OID 0)
-- Dependencies: 232
-- Name: grupos_financeiros_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grupos_financeiros_id_seq OWNED BY public.grupos_financeiros.id;


--
-- TOC entry 235 (class 1259 OID 16753)
-- Name: movimentos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movimentos (
    valor real NOT NULL,
    centro_custo_id bigint NOT NULL,
    conta_id bigint NOT NULL,
    data_hora timestamp(6) without time zone NOT NULL,
    id bigint NOT NULL,
    rubrica_id bigint NOT NULL,
    origem_destino character varying(255),
    tipo character varying(255) NOT NULL,
    CONSTRAINT movimentos_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['ENTRADA'::character varying, 'SAIDA'::character varying])::text[])))
);


--
-- TOC entry 234 (class 1259 OID 16752)
-- Name: movimentos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.movimentos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3588 (class 0 OID 0)
-- Dependencies: 234
-- Name: movimentos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.movimentos_id_seq OWNED BY public.movimentos.id;


--
-- TOC entry 237 (class 1259 OID 16763)
-- Name: reconciliacao_bancaria; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reconciliacao_bancaria (
    saldo numeric(38,2) NOT NULL,
    conta_financeira_id bigint NOT NULL,
    id bigint NOT NULL,
    reconciliacao_mensal_id bigint NOT NULL
);


--
-- TOC entry 236 (class 1259 OID 16762)
-- Name: reconciliacao_bancaria_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reconciliacao_bancaria_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3589 (class 0 OID 0)
-- Dependencies: 236
-- Name: reconciliacao_bancaria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reconciliacao_bancaria_id_seq OWNED BY public.reconciliacao_bancaria.id;


--
-- TOC entry 239 (class 1259 OID 16770)
-- Name: reconciliacao_mensal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reconciliacao_mensal (
    ano integer NOT NULL,
    mes integer NOT NULL,
    saldo_final numeric(38,2) NOT NULL,
    saldo_sugerido numeric(38,2) DEFAULT 0.00 NOT NULL,
    total_entradas numeric(38,2) NOT NULL,
    total_saidas numeric(38,2) NOT NULL,
    data_reconciliacao timestamp(6) without time zone NOT NULL,
    id bigint NOT NULL,
    observacoes character varying(500)
);


--
-- TOC entry 238 (class 1259 OID 16769)
-- Name: reconciliacao_mensal_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reconciliacao_mensal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3590 (class 0 OID 0)
-- Dependencies: 238
-- Name: reconciliacao_mensal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reconciliacao_mensal_id_seq OWNED BY public.reconciliacao_mensal.id;


--
-- TOC entry 241 (class 1259 OID 16780)
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


--
-- TOC entry 240 (class 1259 OID 16779)
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3591 (class 0 OID 0)
-- Dependencies: 240
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- TOC entry 243 (class 1259 OID 16789)
-- Name: rubricas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rubricas (
    valor_padrao real NOT NULL,
    centro_custo_id bigint NOT NULL,
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    tipo character varying(255) NOT NULL,
    CONSTRAINT rubricas_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['RECEITA'::character varying, 'DESPESA'::character varying])::text[])))
);


--
-- TOC entry 242 (class 1259 OID 16788)
-- Name: rubricas_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.rubricas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3592 (class 0 OID 0)
-- Dependencies: 242
-- Name: rubricas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.rubricas_id_seq OWNED BY public.rubricas.id;


--
-- TOC entry 245 (class 1259 OID 16799)
-- Name: socios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.socios (
    data_cadastro date NOT NULL,
    data_nascimento date,
    grau integer NOT NULL,
    grupo_mensalidade_id bigint,
    id bigint NOT NULL,
    socio_titular_id bigint,
    usuario_id bigint,
    celular character varying(20),
    telefone_residencial character varying(20),
    email_alternativo character varying(100),
    cpf character varying(255) NOT NULL,
    endereco_residencial character varying(255),
    nome character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT socios_status_check CHECK (((status)::text = ANY ((ARRAY['FREQUENTE'::character varying, 'INATIVO'::character varying, 'AFASTADO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- TOC entry 244 (class 1259 OID 16798)
-- Name: socios_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.socios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3593 (class 0 OID 0)
-- Dependencies: 244
-- Name: socios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.socios_id_seq OWNED BY public.socios.id;


--
-- TOC entry 247 (class 1259 OID 16813)
-- Name: transacoes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.transacoes (
    data date,
    valor numeric(38,2),
    id bigint NOT NULL,
    relacionado_id bigint,
    descricao character varying(255),
    documento character varying(255),
    fornecedor_ou_socio character varying(255),
    lancado character varying(255),
    tipo character varying(255),
    tipo_relacionamento character varying(255),
    CONSTRAINT transacoes_lancado_check CHECK (((lancado)::text = ANY ((ARRAY['LANCADO'::character varying, 'NAOLANCADO'::character varying])::text[]))),
    CONSTRAINT transacoes_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['CREDITO'::character varying, 'DEBITO'::character varying])::text[]))),
    CONSTRAINT transacoes_tipo_relacionamento_check CHECK (((tipo_relacionamento)::text = ANY ((ARRAY['SOCIO'::character varying, 'FORNECEDOR'::character varying, 'NAO_ENCONTRADO'::character varying])::text[])))
);


--
-- TOC entry 246 (class 1259 OID 16812)
-- Name: transacoes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.transacoes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3594 (class 0 OID 0)
-- Dependencies: 246
-- Name: transacoes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.transacoes_id_seq OWNED BY public.transacoes.id;


--
-- TOC entry 248 (class 1259 OID 16824)
-- Name: usuario_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario_roles (
    role_id bigint NOT NULL,
    usuario_id bigint NOT NULL
);


--
-- TOC entry 250 (class 1259 OID 16830)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    socio_id bigint,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);


--
-- TOC entry 249 (class 1259 OID 16829)
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3595 (class 0 OID 0)
-- Dependencies: 249
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- TOC entry 3295 (class 2604 OID 16677)
-- Name: centro_custo id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.centro_custo ALTER COLUMN id SET DEFAULT nextval('public.centro_custo_id_seq'::regclass);


--
-- TOC entry 3296 (class 2604 OID 16686)
-- Name: cobrancas id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas ALTER COLUMN id SET DEFAULT nextval('public.cobrancas_id_seq'::regclass);


--
-- TOC entry 3297 (class 2604 OID 16698)
-- Name: conta_financeira id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.conta_financeira ALTER COLUMN id SET DEFAULT nextval('public.conta_financeira_id_seq'::regclass);


--
-- TOC entry 3298 (class 2604 OID 16707)
-- Name: contas_pagar id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contas_pagar ALTER COLUMN id SET DEFAULT nextval('public.contas_pagar_id_seq'::regclass);


--
-- TOC entry 3299 (class 2604 OID 16722)
-- Name: fornecedores id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fornecedores ALTER COLUMN id SET DEFAULT nextval('public.fornecedores_id_seq'::regclass);


--
-- TOC entry 3300 (class 2604 OID 16731)
-- Name: grupo_mensalidade id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupo_mensalidade ALTER COLUMN id SET DEFAULT nextval('public.grupo_mensalidade_id_seq'::regclass);


--
-- TOC entry 3301 (class 2604 OID 16738)
-- Name: grupo_mensalidade_rubrica id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupo_mensalidade_rubrica ALTER COLUMN id SET DEFAULT nextval('public.grupo_mensalidade_rubrica_id_seq'::regclass);


--
-- TOC entry 3302 (class 2604 OID 16745)
-- Name: grupos_financeiros id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupos_financeiros ALTER COLUMN id SET DEFAULT nextval('public.grupos_financeiros_id_seq'::regclass);


--
-- TOC entry 3303 (class 2604 OID 16756)
-- Name: movimentos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentos ALTER COLUMN id SET DEFAULT nextval('public.movimentos_id_seq'::regclass);


--
-- TOC entry 3304 (class 2604 OID 16766)
-- Name: reconciliacao_bancaria id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reconciliacao_bancaria ALTER COLUMN id SET DEFAULT nextval('public.reconciliacao_bancaria_id_seq'::regclass);


--
-- TOC entry 3306 (class 2604 OID 16774)
-- Name: reconciliacao_mensal id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reconciliacao_mensal ALTER COLUMN id SET DEFAULT nextval('public.reconciliacao_mensal_id_seq'::regclass);


--
-- TOC entry 3307 (class 2604 OID 16783)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- TOC entry 3308 (class 2604 OID 16792)
-- Name: rubricas id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rubricas ALTER COLUMN id SET DEFAULT nextval('public.rubricas_id_seq'::regclass);


--
-- TOC entry 3309 (class 2604 OID 16802)
-- Name: socios id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios ALTER COLUMN id SET DEFAULT nextval('public.socios_id_seq'::regclass);


--
-- TOC entry 3310 (class 2604 OID 16816)
-- Name: transacoes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transacoes ALTER COLUMN id SET DEFAULT nextval('public.transacoes_id_seq'::regclass);


--
-- TOC entry 3311 (class 2604 OID 16833)
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- TOC entry 3542 (class 0 OID 16672)
-- Dependencies: 218
-- Data for Name: centro_custo; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.centro_custo VALUES (true, 0, 0, 1, 'TAXA DE ENCONTRO DOS PAIS');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 2, 'REPASSE REGIONAL');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 3, 'REPASSE NACIONAL');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 4, 'TARIFAS BANCÁRIAS - SAQUE');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 5, 'DESPESAS DE MANUTENÇÃO');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 6, 'IMPOSTO ISS');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 7, 'CONCESSIONÁRIA DE ENERGIA ELÉTRICA');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 8, 'CONSTRUÇÃO');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 9, 'BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 10, 'MANUTENÇÃO UNIDADE');
INSERT INTO public.centro_custo VALUES (true, 0, 0, 11, 'ORIENTAÇÃO ESPIRITUAL');


--
-- TOC entry 3544 (class 0 OID 16683)
-- Dependencies: 220
-- Data for Name: cobrancas; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3546 (class 0 OID 16695)
-- Dependencies: 222
-- Data for Name: conta_financeira; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.conta_financeira VALUES (0.01, 1, 'BANCO CORA');


--
-- TOC entry 3548 (class 0 OID 16704)
-- Dependencies: 224
-- Data for Name: contas_pagar; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3549 (class 0 OID 16713)
-- Dependencies: 225
-- Data for Name: fornecedor_enderecos; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3551 (class 0 OID 16719)
-- Dependencies: 227
-- Data for Name: fornecedores; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fornecedores VALUES (true, '2025-09-07', 1, '', '06.840.748/0001-89', '', '', 'Equatorial');


--
-- TOC entry 3553 (class 0 OID 16728)
-- Dependencies: 229
-- Data for Name: grupo_mensalidade; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.grupo_mensalidade VALUES (1, 'GRUPO MENSALIDADE BASICO - 135');
INSERT INTO public.grupo_mensalidade VALUES (2, 'GRUPO MENSALIDADE BASICO OE - 145');
INSERT INTO public.grupo_mensalidade VALUES (3, 'GRUPO MENSALIDADE MINIMO - 80');
INSERT INTO public.grupo_mensalidade VALUES (4, 'GRUPO MENSALIDADE 80');
INSERT INTO public.grupo_mensalidade VALUES (6, 'GRUPO MENSALIDADE BASICO 120');
INSERT INTO public.grupo_mensalidade VALUES (7, 'GRUPO MENSALIDADE  MINIMO- 55');
INSERT INTO public.grupo_mensalidade VALUES (8, 'GRUPO MENSALIDADE CESTA  BENEFICENCIA  - 165');
INSERT INTO public.grupo_mensalidade VALUES (9, 'GRUPO MENSALIDADE  SUPER MINIMO -30');
INSERT INTO public.grupo_mensalidade VALUES (10, 'GRUPO MENSALIDADE BASICO 50');
INSERT INTO public.grupo_mensalidade VALUES (11, 'GRUPO MENSALIDADE  150');
INSERT INTO public.grupo_mensalidade VALUES (12, 'GRUPO MENSALIDADE 90');
INSERT INTO public.grupo_mensalidade VALUES (5, 'GRUPO MENSALIDADE  MINIMO- 75');


--
-- TOC entry 3555 (class 0 OID 16735)
-- Dependencies: 231
-- Data for Name: grupo_mensalidade_rubrica; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 9, 1, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 9, 2, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (18, 9, 3, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 9, 4, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 9, 5, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 1, 6, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 1, 7, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 1, 8, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 1, 9, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (123, 1, 10, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 4, 11, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (68, 4, 12, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 4, 13, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 4, 14, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 4, 15, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 2, 16, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 2, 17, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (10, 2, 18, 6);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 2, 19, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 2, 20, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (123, 2, 21, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (30, 8, 22, 15);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 8, 23, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 8, 24, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 8, 25, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 8, 26, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (123, 8, 27, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 3, 28, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (68, 3, 29, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 3, 30, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 3, 31, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 3, 32, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 7, 33, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 7, 34, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (43, 7, 35, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 7, 36, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 7, 37, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 6, 43, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (108, 6, 44, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 6, 45, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 6, 46, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 6, 47, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 10, 48, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (38, 10, 49, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 10, 50, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 10, 51, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 10, 52, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (128, 11, 53, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 11, 54, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (10, 11, 55, 6);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 11, 56, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 11, 57, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 11, 58, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 12, 59, 8);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (78, 12, 60, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 12, 61, 3);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 12, 62, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 12, 63, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (0.3, 5, 69, 4);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (4.8, 5, 70, 2);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (1.6, 5, 71, 1);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (63, 5, 72, 5);
INSERT INTO public.grupo_mensalidade_rubrica VALUES (5.3, 5, 73, 3);


--
-- TOC entry 3557 (class 0 OID 16742)
-- Dependencies: 233
-- Data for Name: grupos_financeiros; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3559 (class 0 OID 16753)
-- Dependencies: 235
-- Data for Name: movimentos; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3561 (class 0 OID 16763)
-- Dependencies: 237
-- Data for Name: reconciliacao_bancaria; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3563 (class 0 OID 16770)
-- Dependencies: 239
-- Data for Name: reconciliacao_mensal; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3565 (class 0 OID 16780)
-- Dependencies: 241
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.roles VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.roles VALUES (2, 'ROLE_TESOUREIRO');
INSERT INTO public.roles VALUES (3, 'ROLE_SOCIO');


--
-- TOC entry 3567 (class 0 OID 16789)
-- Dependencies: 243
-- Data for Name: rubricas; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.rubricas VALUES (1.6, 2, 1, 'FUNDO REGIONAL', 'RECEITA');
INSERT INTO public.rubricas VALUES (4.8, 3, 2, 'PLANTIO', 'RECEITA');
INSERT INTO public.rubricas VALUES (5.3, 3, 3, 'BENEFICÊNCIA', 'RECEITA');
INSERT INTO public.rubricas VALUES (0.3, 3, 4, 'FUNDO DE RESERVA', 'RECEITA');
INSERT INTO public.rubricas VALUES (50, 10, 5, 'MENSALIDADE', 'RECEITA');
INSERT INTO public.rubricas VALUES (0, 11, 6, 'ORIENTAÇÃO ESPIRITUAL', 'RECEITA');
INSERT INTO public.rubricas VALUES (0, 1, 7, 'TAXA DE ENCONTRO DOS PAIS', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 2, 8, 'REPASSE REGIONAL', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 3, 9, 'REPASSE NACIONAL', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 10, 10, 'TARIFAS BANCÁRIAS - SAQUE', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 10, 11, 'DESPESAS DE MANUTENÇÃO', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 6, 12, 'IMPOSTO ISS', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 10, 13, 'CONCESSIONÁRIA DE ENERGIA ELÉTRICA', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 8, 14, 'DESPESAS DE CONSTRUÇÃO', 'DESPESA');
INSERT INTO public.rubricas VALUES (0, 9, 15, 'BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR', 'RECEITA');


--
-- TOC entry 3569 (class 0 OID 16799)
-- Dependencies: 245
-- Data for Name: socios; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.socios VALUES ('2025-09-09', '1979-01-30', 1, NULL, 3, NULL, NULL, '55 (86) 9 9965-0685', '55 (86)  3322-8230', '', '80822193353', '"RUA FRANCISCO GONCALVES', 'MARCELO DE CARVALHO FILGUEIRAS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1959-10-26', 2, NULL, 4, NULL, NULL, '55 (88)  9998-8444', '', '', '15543870353', '"RUA JOSÉ GABRIEL DA COSTA', 'JOSÉ MARQUES MACIEL', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1977-12-24', 2, NULL, 5, NULL, NULL, '55 (84)  8704-8200', '', '', '03059035431', '"RUA.  PONTA DE SERRAMBI - Nº 2298', 'MANUELLA FERNANDES DIAS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1972-01-16', 2, NULL, 6, NULL, NULL, '55 (88)  9995-8181', '', '', '46621733372', '"RUA JOSÉ GABRIEL DA COSTA', 'RACHEL CARVALHO GOMES MACIEL', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1983-01-06', 2, NULL, 7, NULL, NULL, '86994591022', '', '', '97164259100', '"RUA COELHO BASTOS N 667', 'RACHEL MENDONÇA DIAS ALVES', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1966-05-12', 2, NULL, 8, NULL, NULL, '55 (84) 9 9498-4024', '', '', '01196204489', '"RUA. PONTA DE SERRAMBI - Nº 2298', 'SERGE ERIC PITTET', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1982-12-18', 3, NULL, 13, NULL, NULL, '55 (86) 9 9941-6031', '55 (86)  3322-8230', '', '98710338349', '"RUA MARIOTE REBELO', 'GRAZIELA DE MORAES RUBIM FILGUEIRAS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1981-08-10', 3, NULL, 15, NULL, NULL, '55 (86) 9 8852-2593', '55 (86)  3321-2814', '', '87552302372', '"RUA DOUTOR MARIO LAGES', 'JOSÉ LEON QUIRINO VASCONCELOS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-05', '1970-07-31', 3, 4, 1, NULL, 3, '55 (88) 9 9997-1047', '', '', '37079913349', '"RUA JOSÉ BEZERRA DA SILVA', 'MARCELO MUNIZ MACEDO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '2000-10-15', 3, NULL, 16, NULL, NULL, '55 (86) 9 9947-8897', '', '', '02658901364', '"QUADRA 12', 'MARINA CANDIDA DE LIMA BENTO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1983-09-16', 3, NULL, 17, NULL, NULL, '55 (86) 9 8836-8566', '', '', '01533562350', '"RUA DR. MÁRIO LAGES', 'MARY ROSE DOS SANTOS VASCONCELOS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1996-02-22', 3, NULL, 19, NULL, NULL, '55 (86) 9 8154-9717', '', '', '02888518201', '"RUA FRANCISCO AIRES', 'SANMADY LIMA DA ROCHA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1970-12-07', 3, NULL, 20, NULL, NULL, '', '', '', '4324adef-8820-4a74-b206-45f3cf2a624a', '"', 'SARA MARIA DE ARAÚJO SANTOS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1984-12-10', 3, NULL, 21, NULL, NULL, '55 (86) 9 9908-3333', '', 'taline.maciel@gmail.com', '00953527328', '"RUA FELIPE FONTINELE 880', 'TALINE MACHADO MACIEL ARAUJO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1984-01-12', 3, NULL, 22, NULL, NULL, '69 (69) 9 9237-9961', '', '', '86173650215', '"RUA ANTONIO LOPES DO NASCIMENTO', 'VANESSA SOUZA DE OLIVEIRA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '2002-12-16', 4, NULL, 23, NULL, NULL, '86981796500', '', '', '04356909305', '"RUA LINCOLN FONTINELE GUIMARÃES', 'ABRAAO BRUNO DE OLIVEIRA MOTA', 'AFASTADO');
INSERT INTO public.socios VALUES ('2025-09-09', '1981-02-20', 4, NULL, 24, NULL, NULL, '', '', '', '00231662319', '"', 'ALAN JONES PORTO AGUIAR', 'AFASTADO');
INSERT INTO public.socios VALUES ('2025-09-09', '2003-10-20', 4, NULL, 28, NULL, NULL, '', '', '', '5c74ccee-68d0-4bb3-a7b7-3a2d115bceaa', '"', 'DAVI GOMES MACÊDO', 'AFASTADO');
INSERT INTO public.socios VALUES ('2025-09-09', '1989-01-16', 4, NULL, 32, NULL, NULL, '55 (86) 9 9860-8486', '55 (86)  3233-6499', '', '03837345335', '"RUA DES. MANOEL CASTELO BRANCO', 'HULDA MARA LUSTOSA PEREIRA DE ARAÚJO', 'AFASTADO');
INSERT INTO public.socios VALUES ('2025-09-09', '2002-08-30', 4, NULL, 33, NULL, NULL, '86995056556', '', 'ismaelfelizardo15@gmail.com', '08273037347', '"RUA DOS BEM-TE-VIS', 'ISMAEL FELIZARDO SOARES DE OLIVEIRA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '2003-01-26', 4, NULL, 34, NULL, NULL, '', '', '', '02847d14-8ff5-44a5-84d4-d6a443a99b92', '"', 'JOÃO VICTOR MACIEL FERREIRA GOMES', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1989-10-28', 4, NULL, 35, NULL, NULL, '55 (86) 9 9803-9661', '', '', '04025901390', '"AV. NOSSA SENHORA DE FÁTIMA', 'LARA CRUZ MIRANDA DA SILVA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1989-05-17', 4, NULL, 36, NULL, NULL, '55 (86) 9 9960-2944', '', '', '02099944369', '"RUA DEOCLECIO BRITO', 'LAURA FELIZARDO SOARES DE OLIVEIRA ARAUJO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1988-01-04', 4, NULL, 37, NULL, NULL, '', '', '', 'f78b0901-83d7-4940-b5df-a55ae23a798d', '"', 'LETÍCIA GUIMARÃES FARIAS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1983-04-25', 4, NULL, 38, NULL, NULL, '', '55 (86) 9 9429-7616', '', '00718760301', '"', 'LIA RAKEL ROCHA DE OLIVEIRA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1975-03-08', 4, NULL, 39, NULL, NULL, '88988280758', '', 'celinapessoa08@gmail.com', '79077641300', '"RUA AUGUSTA VERAS. 02 - CENTRO', 'MARIA CELINA PEREIRA PESSOA', 'AFASTADO');
INSERT INTO public.socios VALUES ('2025-09-09', '1959-07-18', 4, NULL, 40, NULL, NULL, '55 (85) 9 8854-3092', '', '', '30151147353', '"AV. MARIA DIAMANTINA VERA', 'MARIA DOS NAVEGANTES GOUVEIA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1999-05-06', 4, NULL, 41, NULL, NULL, '+55 (86) 9 9528-1971', '', '', '07579821311', '"AVENIDA MARIA DIAMANTINA VERAS', 'MARIA TAINARA PEREIRA PESSOA GOUVEIA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1983-08-25', 4, NULL, 42, NULL, NULL, '55 (86) 9 9925-2049', '', '', '95603727349', '"RUA DARCI ARAUJO', 'NEYLON ARAUJO SILVA', 'AFASTADO');
INSERT INTO public.socios VALUES ('2025-09-09', '1949-06-27', 4, NULL, 43, NULL, NULL, '+55 (86)  9519-3230', '', '', 'ab15c479-ac67-45c2-81ff-6cd4c29e9d0e', '"', 'NÚBIA QUIRINO VASCONCELOS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1976-04-01', 4, NULL, 44, NULL, NULL, '', '', '', '62810545391', '"', 'PAULA ANDRÉA GOMES MACEDO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1981-03-04', 4, NULL, 45, NULL, NULL, '55 (86) 9 9981-1801', '', '', '03889674470', '"RUA DES MANOEL CASTELO BRANCO', 'ROMULO PAULO CORDAO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1987-12-18', 4, NULL, 46, NULL, NULL, '55 (86) 9 9930-2107', '', '', '01363641360', '"RUA ESPERANZA FONTENELE DE CARVALHO', 'THOMPSOM THAUZER RODRIGUES DE ARAÚJO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1987-04-01', 4, NULL, 47, NULL, NULL, '', '', '', 'cece3e26-7dd3-4aca-a5e7-e30ba6318dea', '"', 'WESLEY RICARDO ROCHA CALIMAN', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '2005-01-10', 4, 12, 25, 4, NULL, '88999958282', '', NULL, '08605739312', '"RUA FRANCISCO ADAMIR DE LIMA', 'ANAMARIA GOMES MACIEL', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1982-12-20', 3, 5, 9, 29, NULL, '', '', NULL, '69bf1f92-62a6-4185-9d8d-315e702c6ea4', '"', 'ADEMAR DAMASCENO SOARES', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1971-09-03', 3, 1, 11, NULL, NULL, '55 (86) 9 9947-0489', '55 (86)  3315-1229', NULL, '25188562855', '"RUA TELIUS FERRAZ', 'ALEXANDRE KEMENES', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1983-10-20', 3, 2, 10, 21, NULL, '55 (86) 9 8823-2098', '', NULL, '65584694304', '"RUA FELIPE FONTINELE 880', 'ALAN SERVIO DE ARAUJO', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1999-02-09', 4, 2, 27, 47, NULL, '', '', NULL, '5cb558d5-f758-4388-9fd5-50dfa13a1ccb', '"', 'ANGELA DE OLIVEIRA BONFIM CALIMAN', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1997-03-09', 4, 10, 26, 36, NULL, '', '', NULL, 'be957fa5-2cf3-4dfa-9c13-856cd0e7644f', '"', 'ANDRE ANDERSON MENDES SOARES DE OLIVEIRA', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1986-07-11', 3, 2, 12, 37, NULL, '55 (85) 9 9911-4511', '', NULL, '02184067385', '"R AFONSO PENA 100 CASA 11', 'BRENO AGUIAR FREITAS', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1984-01-12', 4, 5, 29, 9, NULL, '', '', NULL, 'd9000718-86cf-428a-8e6f-f788eec4d91d', '"', 'DEYRES KENNIA LIMA SOARES', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1987-12-02', 1, 8, 2, 19, NULL, '55 (86) 9 8855-4521', '', NULL, '93102615287', '"RUA FRANCISCO AYRES', 'EMANUEL LUIS DE MESSIAS ALVES', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1987-04-25', 4, 1, 30, NULL, NULL, '', '', NULL, '706b8abe-12c0-49a1-8039-bdbb56e2ffe3', '"', 'Francisco José de Brito Junior', 'FREQUENTE');
INSERT INTO public.socios VALUES ('2025-09-09', '1989-01-16', 4, 1, 31, NULL, NULL, '86981362483', '', NULL, '03a9e0b2-a966-438e-aae9-d91a5a134440', '"RUA MOACY CORREIA RODRIGUES', 'FRANCISCO WELLINGTON SILVA OLIVEIRA', 'FREQUENTE');


--
-- TOC entry 3571 (class 0 OID 16813)
-- Dependencies: 247
-- Data for Name: transacoes; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3572 (class 0 OID 16824)
-- Dependencies: 248
-- Data for Name: usuario_roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.usuario_roles VALUES (1, 1);
INSERT INTO public.usuario_roles VALUES (2, 1);
INSERT INTO public.usuario_roles VALUES (2, 2);
INSERT INTO public.usuario_roles VALUES (2, 3);
INSERT INTO public.usuario_roles VALUES (3, 4);
INSERT INTO public.usuario_roles VALUES (3, 5);
INSERT INTO public.usuario_roles VALUES (3, 6);
INSERT INTO public.usuario_roles VALUES (3, 7);
INSERT INTO public.usuario_roles VALUES (3, 8);
INSERT INTO public.usuario_roles VALUES (3, 9);
INSERT INTO public.usuario_roles VALUES (3, 10);
INSERT INTO public.usuario_roles VALUES (3, 11);
INSERT INTO public.usuario_roles VALUES (3, 12);
INSERT INTO public.usuario_roles VALUES (3, 13);
INSERT INTO public.usuario_roles VALUES (3, 14);
INSERT INTO public.usuario_roles VALUES (3, 15);
INSERT INTO public.usuario_roles VALUES (3, 16);
INSERT INTO public.usuario_roles VALUES (3, 17);
INSERT INTO public.usuario_roles VALUES (3, 18);
INSERT INTO public.usuario_roles VALUES (3, 19);
INSERT INTO public.usuario_roles VALUES (3, 20);
INSERT INTO public.usuario_roles VALUES (3, 21);
INSERT INTO public.usuario_roles VALUES (3, 22);
INSERT INTO public.usuario_roles VALUES (3, 23);
INSERT INTO public.usuario_roles VALUES (3, 24);
INSERT INTO public.usuario_roles VALUES (3, 25);
INSERT INTO public.usuario_roles VALUES (3, 26);
INSERT INTO public.usuario_roles VALUES (3, 27);
INSERT INTO public.usuario_roles VALUES (3, 28);
INSERT INTO public.usuario_roles VALUES (3, 29);
INSERT INTO public.usuario_roles VALUES (3, 30);
INSERT INTO public.usuario_roles VALUES (3, 31);
INSERT INTO public.usuario_roles VALUES (3, 32);
INSERT INTO public.usuario_roles VALUES (3, 33);
INSERT INTO public.usuario_roles VALUES (3, 34);
INSERT INTO public.usuario_roles VALUES (3, 35);
INSERT INTO public.usuario_roles VALUES (3, 36);
INSERT INTO public.usuario_roles VALUES (3, 37);
INSERT INTO public.usuario_roles VALUES (3, 38);
INSERT INTO public.usuario_roles VALUES (3, 39);
INSERT INTO public.usuario_roles VALUES (3, 40);
INSERT INTO public.usuario_roles VALUES (3, 41);
INSERT INTO public.usuario_roles VALUES (3, 42);
INSERT INTO public.usuario_roles VALUES (3, 43);
INSERT INTO public.usuario_roles VALUES (3, 44);
INSERT INTO public.usuario_roles VALUES (3, 45);
INSERT INTO public.usuario_roles VALUES (3, 46);
INSERT INTO public.usuario_roles VALUES (3, 47);


--
-- TOC entry 3574 (class 0 OID 16830)
-- Dependencies: 250
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.usuarios VALUES (1, NULL, '$2a$10$UWvBLdQp4klQiiVdQJVDXeiLx8GALI5JC4Wb86t3a61h.MD3cO0aO', 'admin@sigest.com');
INSERT INTO public.usuarios VALUES (2, NULL, '$2a$10$tdBrwzzlLQqs6xnbeKzbKOU24n7a/8w0fvlYG1hrcwQdrQkE5OTDi', 'tesoureiro');
INSERT INTO public.usuarios VALUES (3, 1, '$2a$10$SxqlhKDj.2h9dWv.pTaex.h/DwHjg9iPhCcCior97GaXPQsYO4sf.', 'marcelo.macedo');
INSERT INTO public.usuarios VALUES (4, 2, '$2a$10$sDG2UAcqFTvhYYjXkwCRz.PqseMaSzFkQkAt34TgvMpAFDi0AuewC', 'curumiemanuel@hotmail.com');
INSERT INTO public.usuarios VALUES (5, 3, '$2a$10$zC8Lb.27aniiWVpMNCvDXOQKMtr8doVvwzFjnt7wMkjRHhaz2Q5mi', 'marcelofilgueiras9@gmail.com');
INSERT INTO public.usuarios VALUES (6, 4, '$2a$10$qk/yIWfglY0LE8NtnqHVbeEQi7CpuHM/GE5Fx94yVjxTbx5sIfyp6', 'macielmarquesjose@outlook.com');
INSERT INTO public.usuarios VALUES (7, 5, '$2a$10$DkxhsMbo/WprBfQbVorWf.KRfiCv.pYyj8Ynuxzkv.Mzm8WWNYw6O', 'cabalafdias@hotmail.com');
INSERT INTO public.usuarios VALUES (8, 6, '$2a$10$ef/7Q/vKVU5AoeZpPWuYb.PYlW5lHhETHx0RXkG9vNaBFYcXzjAMu', 'carvalhogomesrachel@yahoo.com.br');
INSERT INTO public.usuarios VALUES (9, 7, '$2a$10$.WfCQu5vc8mEw8f8psIP9.bARoPIkcjC8KChQa/Zymxvpm07JUI.W', 'rachelmddias@gmail.com');
INSERT INTO public.usuarios VALUES (10, 8, '$2a$10$/Aa5lC3hbXX1eoGIwT9LiuieuP36fPyV4l21Kh4p.kh1S/ZroyMdy', 'sergepittet12@hotmail.com');
INSERT INTO public.usuarios VALUES (11, 9, '$2a$10$gGZ0f9iqXKFQRLXMVrfB4OwBj3ps.ATYiyiozAcG3Xw4FJAUNe3Ba', 'b1b29225-0b5e-4b6d-ab40-fc12f684bc54');
INSERT INTO public.usuarios VALUES (12, 10, '$2a$10$82ReCUFGi7CZA4qkkaRya.t/IC73mv9EBV2kwnx4bZ.91IMX1tm5.', 'servioalan@gmail.com');
INSERT INTO public.usuarios VALUES (13, 11, '$2a$10$qrA2ErjllovaFTOWOsXmE.1.gj/wLgIQ4yMor78txZUsWvVaZN/tm', 'alexandre.kemenes@gmail.com');
INSERT INTO public.usuarios VALUES (14, 12, '$2a$10$8Rf839y5UWCDKT.esLBP3.AqmJq6IYzXH9.wwJIcC..Rvjo908EC6', '5d24da77-5c5c-4419-a18b-da14786962cc');
INSERT INTO public.usuarios VALUES (15, 13, '$2a$10$jYJnd0LwM0eotNjftbme4..c4WRE9LQn2A1u8msTA3nzvQueqJaYO', 'grazielamrfilgueiras@hotmail.com');
INSERT INTO public.usuarios VALUES (16, 15, '$2a$10$MVSnJuSSAN3UVBbue2kD.OiG4P.UjstHmjFO8J.n9WMOig8sEUUtS', 'joseleon.vasconcelos@gmail.com');
INSERT INTO public.usuarios VALUES (17, 16, '$2a$10$cAHNOpGXozOsoNsasYd4fu5JAbN6ASwN0H0JxPRN7muhLrKXkWgmu', 'marinalima101@hotmail.com');
INSERT INTO public.usuarios VALUES (18, 17, '$2a$10$msRFyT7Qc014THkkxGEi0uktM9jjPXPlmnWlFlYDFuFCwkp14P4H2', 'melzinha.leon@gmail.com');
INSERT INTO public.usuarios VALUES (19, 19, '$2a$10$1WZBI8bXKuQB6S.95sbt8./M5.O.lHTTEeHqrZ4CnZPqqP5892uE6', 'sanmadytk@gmail.com');
INSERT INTO public.usuarios VALUES (20, 20, '$2a$10$yuWd6vXOYISb.3rl42g8JeQhBPc.mcd970zVYz/eeRtEAgD9rPebG', 'bee560ef-2031-4b76-a286-b9fb72aeba15');
INSERT INTO public.usuarios VALUES (21, 21, '$2a$10$9ZsSmPivP5jMOnoroTwvuufNkGbm9eL49VFhQaTQdHVn58bbkLbE.', 'e5aca530-f697-42e1-8044-cc9214473ae6');
INSERT INTO public.usuarios VALUES (22, 22, '$2a$10$0zJmMzIhLulyaijSlQudm.lGTAnZi/W3jNQEHPoJX381fOnZCk1JG', 'vanessajafraroyal@gmail.com');
INSERT INTO public.usuarios VALUES (23, 23, '$2a$10$pLbS6XHNwYNnvlEXMlj/A./RRUmi8tpsNQqrSrZ7ZrCjPxO9nKxA2', 'abraobruno76@gmail.com');
INSERT INTO public.usuarios VALUES (24, 24, '$2a$10$1CvMf6snHeBgfz5O5Cb8ZeGgYrCmB2ED8tuLnlEC/suGZQwg3gWnu', 'portoaguiaralanjones@gmail.com');
INSERT INTO public.usuarios VALUES (25, 25, '$2a$10$VLvsf7RntwAEEiI8PX22GOolAifZJpQ7khwKillpgUWNXPDyZTIra', 'anamariagomeesmaciel@gmail.com');
INSERT INTO public.usuarios VALUES (26, 26, '$2a$10$KTUdOL3.TFedTfZmxjwVAOiY7vYv2MyQtpFz4itsoHfh35XSTqMsG', '473b1e93-8397-4e1e-8815-3ab0f4ac3585');
INSERT INTO public.usuarios VALUES (27, 27, '$2a$10$NOC3BR7Xx5LPAJhYiAXR/.aAVVBAki6PXf.hl9L9EYM0SuWNYtMQC', 'angelabomfim16@gmail.com');
INSERT INTO public.usuarios VALUES (28, 28, '$2a$10$bKx4IVF0C7TDicm1coo.TOfGUjTHyj3br7B.J/3pZCUmnrXV5yWHW', '249a79ee-d578-4107-b3c5-1eaa8b602fbc');
INSERT INTO public.usuarios VALUES (29, 29, '$2a$10$xtYscceDiYsSXRzuioLDye7cAL16uIpLIotDKocULze2hPm2.6B0O', '76639b76-4e14-4665-8f3e-57ee1c1ae53f');
INSERT INTO public.usuarios VALUES (30, 30, '$2a$10$UAwMoedsmy4YX619to7G6eUfxlU3En7Z72yx6iEq6qrqFbPVerWHK', '28b75a18-ecb1-4156-a070-a4735ad4b36d');
INSERT INTO public.usuarios VALUES (31, 31, '$2a$10$8NXPk9OraBbfWtG15UpsR.JizPxm6IAEToBiq20TG/khkh0ncSxLW', 'oliveirawellington241@gmail.com');
INSERT INTO public.usuarios VALUES (32, 32, '$2a$10$oKK5ckgT3FiJr/3bAG17dOqkKPDutsTtUIwtqef3pOZvUguYTqiT2', 'hulda_mara@hotmail.com');
INSERT INTO public.usuarios VALUES (33, 33, '$2a$10$O93o4hPdtxNC6GTjSBS0Nur2YHD19JHuBZlcFY2bSx.RTb6Q1e6u6', '2c9758e8-1bc4-46e0-8141-a002ac0dee6f');
INSERT INTO public.usuarios VALUES (34, 34, '$2a$10$ggauVpCIwvRsKTZHOYRe9.Vy6/ZSMtnlXzGwbp71aqN8B6qK727VS', 'joao_victor_gomes@outlook.com');
INSERT INTO public.usuarios VALUES (35, 35, '$2a$10$ZhoY0nhY9BdSTmZwWDV.FOJDXNtXQXPIBKnZBTy98W.ULyOcvD.oq', 'lahramiranda@hotmail.com');
INSERT INTO public.usuarios VALUES (36, 36, '$2a$10$1dEvrmoMjM6jicFPQ7ahN.NJu00mtbsl9yf84UEeaHF2KMqMXIvSq', 'feliz.laura17@gmail.com');
INSERT INTO public.usuarios VALUES (37, 37, '$2a$10$.vqxt/a0b9T0tL58Us6xW.Ay3m6sMrn6FU7jga9zk.l7oo0xHX/oG', 'bd57ffa6-6e1a-4dd6-be6e-900baf3baf5c');
INSERT INTO public.usuarios VALUES (38, 38, '$2a$10$BIYuXWSg6V/J1PAPGxyDJ.NUSWKDOknIaSNbpjvJ70inSf98x2mGa', 'liarakelmed2018@gmail.com');
INSERT INTO public.usuarios VALUES (39, 39, '$2a$10$cNuzrb9HR5kLGa/yIDVMCeOVEQEu3lU8sPH9n5Jj6xKcXWoJ7PTVm', '607b69ea-d1c3-4fad-976f-7cf7903154c8');
INSERT INTO public.usuarios VALUES (40, 40, '$2a$10$Hxs/Xsbz9K.lwDAm28XUfOAAZpBZA8nMyG3kEcIjAGnmfW3ge/xY.', 'd3d83736-95de-41b3-9e0a-de863a14325e');
INSERT INTO public.usuarios VALUES (41, 41, '$2a$10$S0gZX4ggyW9ZM.fiplH5CO9vriN1mWRf3HEMWfB9LPtezQ/hsl5IW', 'tainarappgouveia@gmail.com');
INSERT INTO public.usuarios VALUES (42, 42, '$2a$10$ORDPNytHsZTd0sJ9rI8GEujls.etMZoLM7XxmwPjtmMbAuQljI5wC', 'neylonaraujosilva@gmail.com');
INSERT INTO public.usuarios VALUES (43, 43, '$2a$10$e3shnJ4o8L2XFbXfd.oq.O243s3JMNbHB5HD.dr0qmurOzj5k9pIi', 'null');
INSERT INTO public.usuarios VALUES (44, 44, '$2a$10$uVkzqgInw/CgwgwY/HVMQezI43MDOYmGQYtP96gfruFAbMz5rCjSS', '96491565-8dcb-4210-95e6-78ab7a9c0c01');
INSERT INTO public.usuarios VALUES (45, 45, '$2a$10$R43q5hPgqsGMy/fYgPUOyufvc1U/NTy/q3qhhCB/HFiRuxel3HKPW', 'romulopaulocordao@gmail.com');
INSERT INTO public.usuarios VALUES (46, 46, '$2a$10$wH9Cs1H0F5cS5sQsux/3GOxQbsJN3Vl2N.zs2VfFUY9q0j5dbU.wy', 'thompsom2016@gmail.com');
INSERT INTO public.usuarios VALUES (47, 47, '$2a$10$8Fld/P1ChxpTnGqxRss7MO7td1ILbPEY3TW0z4/acSgCBis3oMXA.', 'wrcaliman@gmail.com');


--
-- TOC entry 3596 (class 0 OID 0)
-- Dependencies: 217
-- Name: centro_custo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.centro_custo_id_seq', 11, true);


--
-- TOC entry 3597 (class 0 OID 0)
-- Dependencies: 219
-- Name: cobrancas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cobrancas_id_seq', 1, false);


--
-- TOC entry 3598 (class 0 OID 0)
-- Dependencies: 221
-- Name: conta_financeira_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.conta_financeira_id_seq', 1, true);


--
-- TOC entry 3599 (class 0 OID 0)
-- Dependencies: 223
-- Name: contas_pagar_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.contas_pagar_id_seq', 1, false);


--
-- TOC entry 3600 (class 0 OID 0)
-- Dependencies: 226
-- Name: fornecedores_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.fornecedores_id_seq', 1, true);


--
-- TOC entry 3601 (class 0 OID 0)
-- Dependencies: 228
-- Name: grupo_mensalidade_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grupo_mensalidade_id_seq', 12, true);


--
-- TOC entry 3602 (class 0 OID 0)
-- Dependencies: 230
-- Name: grupo_mensalidade_rubrica_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grupo_mensalidade_rubrica_id_seq', 73, true);


--
-- TOC entry 3603 (class 0 OID 0)
-- Dependencies: 232
-- Name: grupos_financeiros_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grupos_financeiros_id_seq', 1, false);


--
-- TOC entry 3604 (class 0 OID 0)
-- Dependencies: 234
-- Name: movimentos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.movimentos_id_seq', 1, false);


--
-- TOC entry 3605 (class 0 OID 0)
-- Dependencies: 236
-- Name: reconciliacao_bancaria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reconciliacao_bancaria_id_seq', 1, false);


--
-- TOC entry 3606 (class 0 OID 0)
-- Dependencies: 238
-- Name: reconciliacao_mensal_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reconciliacao_mensal_id_seq', 1, false);


--
-- TOC entry 3607 (class 0 OID 0)
-- Dependencies: 240
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roles_id_seq', 3, true);


--
-- TOC entry 3608 (class 0 OID 0)
-- Dependencies: 242
-- Name: rubricas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.rubricas_id_seq', 15, true);


--
-- TOC entry 3609 (class 0 OID 0)
-- Dependencies: 244
-- Name: socios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.socios_id_seq', 47, true);


--
-- TOC entry 3610 (class 0 OID 0)
-- Dependencies: 246
-- Name: transacoes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.transacoes_id_seq', 1, false);


--
-- TOC entry 3611 (class 0 OID 0)
-- Dependencies: 249
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 47, true);


--
-- TOC entry 3323 (class 2606 OID 16681)
-- Name: centro_custo centro_custo_nome_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.centro_custo
    ADD CONSTRAINT centro_custo_nome_key UNIQUE (nome);


--
-- TOC entry 3325 (class 2606 OID 16679)
-- Name: centro_custo centro_custo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.centro_custo
    ADD CONSTRAINT centro_custo_pkey PRIMARY KEY (id);


--
-- TOC entry 3327 (class 2606 OID 16693)
-- Name: cobrancas cobrancas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas
    ADD CONSTRAINT cobrancas_pkey PRIMARY KEY (id);


--
-- TOC entry 3329 (class 2606 OID 16702)
-- Name: conta_financeira conta_financeira_nome_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.conta_financeira
    ADD CONSTRAINT conta_financeira_nome_key UNIQUE (nome);


--
-- TOC entry 3331 (class 2606 OID 16700)
-- Name: conta_financeira conta_financeira_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.conta_financeira
    ADD CONSTRAINT conta_financeira_pkey PRIMARY KEY (id);


--
-- TOC entry 3333 (class 2606 OID 16712)
-- Name: contas_pagar contas_pagar_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contas_pagar
    ADD CONSTRAINT contas_pagar_pkey PRIMARY KEY (id);


--
-- TOC entry 3335 (class 2606 OID 16726)
-- Name: fornecedores fornecedores_cnpj_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fornecedores
    ADD CONSTRAINT fornecedores_cnpj_key UNIQUE (cnpj);


--
-- TOC entry 3337 (class 2606 OID 16724)
-- Name: fornecedores fornecedores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fornecedores
    ADD CONSTRAINT fornecedores_pkey PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 16733)
-- Name: grupo_mensalidade grupo_mensalidade_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupo_mensalidade
    ADD CONSTRAINT grupo_mensalidade_pkey PRIMARY KEY (id);


--
-- TOC entry 3341 (class 2606 OID 16740)
-- Name: grupo_mensalidade_rubrica grupo_mensalidade_rubrica_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupo_mensalidade_rubrica
    ADD CONSTRAINT grupo_mensalidade_rubrica_pkey PRIMARY KEY (id);


--
-- TOC entry 3343 (class 2606 OID 16751)
-- Name: grupos_financeiros grupos_financeiros_nome_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupos_financeiros
    ADD CONSTRAINT grupos_financeiros_nome_key UNIQUE (nome);


--
-- TOC entry 3345 (class 2606 OID 16749)
-- Name: grupos_financeiros grupos_financeiros_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupos_financeiros
    ADD CONSTRAINT grupos_financeiros_pkey PRIMARY KEY (id);


--
-- TOC entry 3347 (class 2606 OID 16761)
-- Name: movimentos movimentos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentos
    ADD CONSTRAINT movimentos_pkey PRIMARY KEY (id);


--
-- TOC entry 3349 (class 2606 OID 16768)
-- Name: reconciliacao_bancaria reconciliacao_bancaria_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reconciliacao_bancaria
    ADD CONSTRAINT reconciliacao_bancaria_pkey PRIMARY KEY (id);


--
-- TOC entry 3351 (class 2606 OID 16778)
-- Name: reconciliacao_mensal reconciliacao_mensal_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reconciliacao_mensal
    ADD CONSTRAINT reconciliacao_mensal_pkey PRIMARY KEY (id);


--
-- TOC entry 3353 (class 2606 OID 16787)
-- Name: roles roles_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- TOC entry 3355 (class 2606 OID 16785)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 3357 (class 2606 OID 16797)
-- Name: rubricas rubricas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rubricas
    ADD CONSTRAINT rubricas_pkey PRIMARY KEY (id);


--
-- TOC entry 3359 (class 2606 OID 16811)
-- Name: socios socios_cpf_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios
    ADD CONSTRAINT socios_cpf_key UNIQUE (cpf);


--
-- TOC entry 3361 (class 2606 OID 16807)
-- Name: socios socios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios
    ADD CONSTRAINT socios_pkey PRIMARY KEY (id);


--
-- TOC entry 3363 (class 2606 OID 16809)
-- Name: socios socios_usuario_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios
    ADD CONSTRAINT socios_usuario_id_key UNIQUE (usuario_id);


--
-- TOC entry 3365 (class 2606 OID 16823)
-- Name: transacoes transacoes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transacoes
    ADD CONSTRAINT transacoes_pkey PRIMARY KEY (id);


--
-- TOC entry 3367 (class 2606 OID 16828)
-- Name: usuario_roles usuario_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_roles
    ADD CONSTRAINT usuario_roles_pkey PRIMARY KEY (role_id, usuario_id);


--
-- TOC entry 3369 (class 2606 OID 16837)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 3371 (class 2606 OID 16839)
-- Name: usuarios usuarios_socio_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_socio_id_key UNIQUE (socio_id);


--
-- TOC entry 3373 (class 2606 OID 16841)
-- Name: usuarios usuarios_username_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_username_key UNIQUE (username);


--
-- TOC entry 3374 (class 2606 OID 16852)
-- Name: cobrancas fk44pepqpwlw0ue4feeehb8mrjb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas
    ADD CONSTRAINT fk44pepqpwlw0ue4feeehb8mrjb FOREIGN KEY (rubrica_id) REFERENCES public.rubricas(id);


--
-- TOC entry 3395 (class 2606 OID 16947)
-- Name: usuarios fk53775udbw33etheo6shj1r9t7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT fk53775udbw33etheo6shj1r9t7 FOREIGN KEY (socio_id) REFERENCES public.socios(id);


--
-- TOC entry 3382 (class 2606 OID 16887)
-- Name: grupo_mensalidade_rubrica fk750no0tqtd66tpqcaorau5waa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupo_mensalidade_rubrica
    ADD CONSTRAINT fk750no0tqtd66tpqcaorau5waa FOREIGN KEY (rubrica_id) REFERENCES public.rubricas(id);


--
-- TOC entry 3375 (class 2606 OID 16847)
-- Name: cobrancas fk7bii5e05u5tr1t78oppg9jnja; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas
    ADD CONSTRAINT fk7bii5e05u5tr1t78oppg9jnja FOREIGN KEY (grupo_mensalidade_id) REFERENCES public.grupo_mensalidade(id);


--
-- TOC entry 3384 (class 2606 OID 16892)
-- Name: movimentos fk92x7wmvg5b6s8mchbl6ea82g1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentos
    ADD CONSTRAINT fk92x7wmvg5b6s8mchbl6ea82g1 FOREIGN KEY (centro_custo_id) REFERENCES public.centro_custo(id);


--
-- TOC entry 3390 (class 2606 OID 16922)
-- Name: socios fka73j6a1wq1g3hdm04pajs5uc4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios
    ADD CONSTRAINT fka73j6a1wq1g3hdm04pajs5uc4 FOREIGN KEY (grupo_mensalidade_id) REFERENCES public.grupo_mensalidade(id);


--
-- TOC entry 3385 (class 2606 OID 16897)
-- Name: movimentos fkadkfsy7qm9g7d6177kc2u2fcg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentos
    ADD CONSTRAINT fkadkfsy7qm9g7d6177kc2u2fcg FOREIGN KEY (conta_id) REFERENCES public.conta_financeira(id);


--
-- TOC entry 3381 (class 2606 OID 16877)
-- Name: fornecedor_enderecos fkc7co7809tqy2vjpcjn0gj42c0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fornecedor_enderecos
    ADD CONSTRAINT fkc7co7809tqy2vjpcjn0gj42c0 FOREIGN KEY (fornecedor_id) REFERENCES public.fornecedores(id);


--
-- TOC entry 3391 (class 2606 OID 16927)
-- Name: socios fkebie2asqjq9xaqxs0ycy2liro; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios
    ADD CONSTRAINT fkebie2asqjq9xaqxs0ycy2liro FOREIGN KEY (socio_titular_id) REFERENCES public.socios(id);


--
-- TOC entry 3379 (class 2606 OID 16867)
-- Name: contas_pagar fkf61n7454tcwwqsao0qibsfvbm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contas_pagar
    ADD CONSTRAINT fkf61n7454tcwwqsao0qibsfvbm FOREIGN KEY (fornecedor_id) REFERENCES public.fornecedores(id);


--
-- TOC entry 3389 (class 2606 OID 16917)
-- Name: rubricas fkfmc0wbr03rtdals6mk966hbuw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rubricas
    ADD CONSTRAINT fkfmc0wbr03rtdals6mk966hbuw FOREIGN KEY (centro_custo_id) REFERENCES public.centro_custo(id);


--
-- TOC entry 3376 (class 2606 OID 16862)
-- Name: cobrancas fkfnol79kc10vyg2ayubmtelpx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas
    ADD CONSTRAINT fkfnol79kc10vyg2ayubmtelpx FOREIGN KEY (transacao_id) REFERENCES public.transacoes(id);


--
-- TOC entry 3387 (class 2606 OID 16912)
-- Name: reconciliacao_bancaria fkhe9yushyjw3adm897lgvbmk2k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reconciliacao_bancaria
    ADD CONSTRAINT fkhe9yushyjw3adm897lgvbmk2k FOREIGN KEY (reconciliacao_mensal_id) REFERENCES public.reconciliacao_mensal(id);


--
-- TOC entry 3383 (class 2606 OID 16882)
-- Name: grupo_mensalidade_rubrica fkhyq0v1hoavlrpuuwfsyl1xl65; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grupo_mensalidade_rubrica
    ADD CONSTRAINT fkhyq0v1hoavlrpuuwfsyl1xl65 FOREIGN KEY (grupo_mensalidade_id) REFERENCES public.grupo_mensalidade(id);


--
-- TOC entry 3388 (class 2606 OID 16907)
-- Name: reconciliacao_bancaria fki2111tn3xnpw4d3rj6h3klhos; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reconciliacao_bancaria
    ADD CONSTRAINT fki2111tn3xnpw4d3rj6h3klhos FOREIGN KEY (conta_financeira_id) REFERENCES public.conta_financeira(id);


--
-- TOC entry 3377 (class 2606 OID 16857)
-- Name: cobrancas fkkxss0vltgk61vu34cjyyhwg6q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas
    ADD CONSTRAINT fkkxss0vltgk61vu34cjyyhwg6q FOREIGN KEY (socio_id) REFERENCES public.socios(id);


--
-- TOC entry 3380 (class 2606 OID 16872)
-- Name: contas_pagar fkott3dr9w6jb6bd00oeo4r64vs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contas_pagar
    ADD CONSTRAINT fkott3dr9w6jb6bd00oeo4r64vs FOREIGN KEY (rubrica_id) REFERENCES public.rubricas(id);


--
-- TOC entry 3378 (class 2606 OID 16842)
-- Name: cobrancas fkq8sg51cc5ap1x0m5attjnfe40; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cobrancas
    ADD CONSTRAINT fkq8sg51cc5ap1x0m5attjnfe40 FOREIGN KEY (fornecedor_id) REFERENCES public.fornecedores(id);


--
-- TOC entry 3386 (class 2606 OID 16902)
-- Name: movimentos fkr9ip2dgwey9vbjo7ju4dg1udn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentos
    ADD CONSTRAINT fkr9ip2dgwey9vbjo7ju4dg1udn FOREIGN KEY (rubrica_id) REFERENCES public.rubricas(id);


--
-- TOC entry 3392 (class 2606 OID 16932)
-- Name: socios fks5xb9xvhktwvwb062sidcu0m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socios
    ADD CONSTRAINT fks5xb9xvhktwvwb062sidcu0m FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- TOC entry 3393 (class 2606 OID 16937)
-- Name: usuario_roles fktk4qndf0xt1ijkk4a7wj5vnwb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_roles
    ADD CONSTRAINT fktk4qndf0xt1ijkk4a7wj5vnwb FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 3394 (class 2606 OID 16942)
-- Name: usuario_roles fkuu9tea04xb29m2km5lwe46ua; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_roles
    ADD CONSTRAINT fkuu9tea04xb29m2km5lwe46ua FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


-- Completed on 2025-09-12 14:30:41

--
-- PostgreSQL database dump complete
--

