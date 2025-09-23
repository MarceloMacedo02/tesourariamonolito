--
-- PostgreSQL database dump
--

\restrict C9pH6uJOgE7wzsu1BDen9qC23xeDpBWcjbke0Tc90BA2a7La53BFiepRdHHuYik

-- Dumped from database version 14.19 (Debian 14.19-1.pgdg13+1)
-- Dumped by pg_dump version 14.19 (Debian 14.19-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: reconciliacao_mensal; Type: TABLE; Schema: public; Owner: tesourario
--

CREATE TABLE public.reconciliacao_mensal (
    id bigint NOT NULL,
    ano integer NOT NULL,
    mes integer NOT NULL,
    saldo_final numeric(38,2),
    saldo_inicial numeric(38,2),
    total_entradas numeric(38,2),
    total_saidas numeric(38,2)
);


ALTER TABLE public.reconciliacao_mensal OWNER TO tesourario;

--
-- Data for Name: reconciliacao_mensal; Type: TABLE DATA; Schema: public; Owner: tesourario
--

COPY public.reconciliacao_mensal (id, ano, mes, saldo_final, saldo_inicial, total_entradas, total_saidas) FROM stdin;
1	2025	8	15741.69	10457.22	15615.00	10330.53
\.


--
-- PostgreSQL database dump complete
--

\unrestrict C9pH6uJOgE7wzsu1BDen9qC23xeDpBWcjbke0Tc90BA2a7La53BFiepRdHHuYik

