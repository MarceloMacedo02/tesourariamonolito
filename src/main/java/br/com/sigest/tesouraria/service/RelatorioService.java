package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.domain.repository.CobrancaRepository;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.ContaPagarRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.ReconciliacaoMensalRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.dto.RelatorioBalanceteDto;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroDto;
import br.com.sigest.tesouraria.dto.RelatorioEntradasDetalhadasDto;
import br.com.sigest.tesouraria.dto.RelatorioFluxoCaixaDto;
import br.com.sigest.tesouraria.dto.SocioInadimplenteDto;

@Service
public class RelatorioService {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioService.class);

    @Autowired
    private MovimentoRepository movimentoRepository;
    @Autowired
    private CobrancaRepository cobrancaRepository;
    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired
    private ContaPagarRepository contaPagarRepository;
    @Autowired
    private ReconciliacaoMensalRepository reconciliacaoMensalRepository;

    /**
     * Gera um balancete para um período específico.
     *
     * @param inicio a data de início do período
     * @param fim    a data de fim do período
     * @return um RelatorioBalanceteDto com o resultado do balancete
     */
    public RelatorioBalanceteDto gerarBalancete(LocalDate inicio, LocalDate fim) {
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();
        BigDecimal receitas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.ENTRADA, inicioDt, fimDt);
        BigDecimal despesas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.SAIDA, inicioDt, fimDt);
        receitas = receitas == null ? BigDecimal.ZERO : receitas;
        despesas = despesas == null ? BigDecimal.ZERO : despesas;
        BigDecimal resultado = receitas.subtract(despesas);
        return new RelatorioBalanceteDto(receitas, despesas, resultado);
    }

    /**
     * Gera uma lista de sócios inadimplentes.
     *
     * @return uma lista de SocioInadimplenteDto
     */
    public List<SocioInadimplenteDto> gerarListaInadimplentes() {
        List<Socio> sociosInadimplentes = socioRepository.findSociosInadimplentes();
        if (sociosInadimplentes.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> socioIds = sociosInadimplentes.stream().map(Socio::getId).collect(Collectors.toList());
        List<Cobranca> cobrancasVencidas = cobrancaRepository.findBySocioIdInAndStatus(socioIds,
                StatusCobranca.VENCIDA);
        Map<Long, List<Cobranca>> cobrancasPorSocio = cobrancasVencidas.stream()
                .collect(Collectors.groupingBy(cobranca -> cobranca.getSocio().getId()));

        return sociosInadimplentes.stream().map(socio -> {
            List<Cobranca> cobrancas = cobrancasPorSocio.getOrDefault(socio.getId(), new ArrayList<>());
            return new SocioInadimplenteDto(socio.getId(), socio.getNome(), cobrancas);
        }).collect(Collectors.toList());
    }

    /**
     * Gera uma projeção do fluxo de caixa.
     *
     * @return um RelatorioFluxoCaixaDto com a projeção do fluxo de caixa
     */
    public RelatorioFluxoCaixaDto gerarProjecaoFluxoCaixa() {
        BigDecimal saldoAtual = contaFinanceiraRepository.sumTotalSaldo();
        BigDecimal projecaoRecebimentos = cobrancaRepository.sumTotalAReceber();
        BigDecimal projecaoPagamentos = contaPagarRepository.sumTotalAPagar();
        saldoAtual = saldoAtual == null ? BigDecimal.ZERO : saldoAtual;
        projecaoRecebimentos = projecaoRecebimentos == null ? BigDecimal.ZERO : projecaoRecebimentos;
        projecaoPagamentos = projecaoPagamentos == null ? BigDecimal.ZERO : projecaoPagamentos;
        BigDecimal saldoProjetado = saldoAtual.add(projecaoRecebimentos).subtract(projecaoPagamentos);
        return new RelatorioFluxoCaixaDto(saldoAtual, projecaoRecebimentos, projecaoPagamentos, saldoProjetado);
    }

    /**
     * Gera um demonstrativo financeiro para um mês e ano específicos.
     *
     * @param mes o mês para gerar o demonstrativo
     * @param ano o ano para gerar o demonstrativo
     * @return um RelatorioDemonstrativoFinanceiroDto com o demonstrativo financeiro
     */
    public RelatorioDemonstrativoFinanceiroDto gerarDemonstrativoFinanceiro(int mes, int ano) {
        // Obter a reconciliação mensal do período solicitado
        List<ReconciliacaoMensal> reconciliacoes = reconciliacaoMensalRepository.findByMesAndAno(mes, ano);
        ReconciliacaoMensal reconciliacaoMensal = reconciliacoes.isEmpty() ? null : reconciliacoes.get(0);
        
        // Se não houver reconciliação, criar uma nova com valores zerados
        if (reconciliacaoMensal == null) {
            reconciliacaoMensal = new ReconciliacaoMensal();
            reconciliacaoMensal.setMes(mes);
            reconciliacaoMensal.setAno(ano);
            reconciliacaoMensal.setSaldoInicial(BigDecimal.ZERO);
            reconciliacaoMensal.setTotalEntradas(BigDecimal.ZERO);
            reconciliacaoMensal.setTotalSaidas(BigDecimal.ZERO);
            reconciliacaoMensal.setSaldoFinal(BigDecimal.ZERO);
        }

        // Saldo do período anterior (saldo inicial da reconciliação)
        BigDecimal saldoPeriodoAnterior = reconciliacaoMensal.getSaldoInicial() != null ? 
            reconciliacaoMensal.getSaldoInicial() : BigDecimal.ZERO;

        // Entradas e Saídas do período (da própria reconciliação)
        BigDecimal totalEntradas = reconciliacaoMensal.getTotalEntradas() != null ? 
            reconciliacaoMensal.getTotalEntradas() : BigDecimal.ZERO;
        BigDecimal totalSaidas = reconciliacaoMensal.getTotalSaidas() != null ? 
            reconciliacaoMensal.getTotalSaidas() : BigDecimal.ZERO;

        // Agrupar movimentos do período para exibição detalhada
        LocalDateTime inicioDoPeriodo = LocalDate.of(ano, mes, 1).atStartOfDay();
        LocalDateTime fimDoPeriodo = LocalDate.of(ano, mes, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59);

        List<Movimento> movimentosDoPeriodo = movimentoRepository.findByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo);
        logger.info("Movimentos do período ({} a {}): {} movimentos encontrados.", inicioDoPeriodo, fimDoPeriodo,
                movimentosDoPeriodo.size());

        List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> entradasAgrupadas = new java.util.ArrayList<>();
        List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> saidasAgrupadas = new java.util.ArrayList<>();

        // Group movements by rubrica type (RECEITA/DESPESA) and then by rubrica name
        java.util.Map<TipoRubrica, java.util.Map<String, BigDecimal>> groupedMovimentos = movimentosDoPeriodo.stream()
                .collect(Collectors.groupingBy(
                        mov -> mov.getRubrica().getTipo(),
                        Collectors.groupingBy(
                                mov -> mov.getRubrica().getNome(),
                                // Usando mov.getValor() diretamente em vez de
                                // BigDecimal.valueOf(mov.getValor())
                                // porque mov.getValor() já retorna um BigDecimal, evitando conversões
                                // desnecessárias
                                Collectors.reducing(BigDecimal.ZERO, mov -> mov.getValor(),
                                        BigDecimal::add))));
        // logger.info("Movimentos agrupados: {}", groupedMovimentos); // Removido para evitar problemas de encoding

        for (java.util.Map.Entry<TipoRubrica, java.util.Map<String, BigDecimal>> entry : groupedMovimentos.entrySet()) {
            TipoRubrica tipoRubrica = entry.getKey();
            java.util.Map<String, BigDecimal> rubricasMap = entry.getValue();

            List<RelatorioDemonstrativoFinanceiroDto.RubricaDetalheDto> rubricasDetalhe = rubricasMap.entrySet()
                    .stream()
                    .map(e -> new RelatorioDemonstrativoFinanceiroDto.RubricaDetalheDto(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            BigDecimal totalCategoria = rubricasMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto rubricaAgrupadaDto = new RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto(
                    tipoRubrica, rubricasDetalhe, totalCategoria);

            if (tipoRubrica == TipoRubrica.RECEITA) {
                entradasAgrupadas.add(rubricaAgrupadaDto);
            } else {
                saidasAgrupadas.add(rubricaAgrupadaDto);
            }
        }

        // Saldo Operacional (saldo inicial + entradas - saídas)
        BigDecimal saldoOperacional = saldoPeriodoAnterior.add(totalEntradas).subtract(totalSaidas);

        // Saldo Final Consolidado (saldo final da reconciliação)
        BigDecimal saldoFinalCaixaBanco = reconciliacaoMensal.getSaldoFinal() != null ? 
            reconciliacaoMensal.getSaldoFinal() : BigDecimal.ZERO;

        RelatorioDemonstrativoFinanceiroDto demonstrativo = new RelatorioDemonstrativoFinanceiroDto();
        demonstrativo.setMes(mes);
        demonstrativo.setAno(ano);
        demonstrativo.setSaldoPeriodoAnterior(saldoPeriodoAnterior);
        demonstrativo.setTotalEntradas(totalEntradas);
        demonstrativo.setTotalSaidas(totalSaidas);
        demonstrativo.setSaldoOperacional(saldoOperacional);
        demonstrativo.setSaldoFinalCaixaBanco(saldoFinalCaixaBanco);
        demonstrativo.setEntradasAgrupadas(entradasAgrupadas);
        demonstrativo.setSaidasAgrupadas(saidasAgrupadas);

        return demonstrativo;
    }

    /**
     * Gera um relatório detalhado das entradas no mês por sócios e rubricas.
     *
     * @param mes o mês para gerar o relatório
     * @param ano o ano para gerar o relatório
     * @return um RelatorioEntradasDetalhadasDto com os dados do relatório
     */
    public RelatorioEntradasDetalhadasDto gerarRelatorioEntradasDetalhadas(int mes, int ano) {
        // Definir o período do mês
        LocalDateTime inicioDoPeriodo = LocalDate.of(ano, mes, 1).atStartOfDay();
        LocalDateTime fimDoPeriodo = LocalDate.of(ano, mes, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59);

        // Obter movimentos de entrada do período
        List<Movimento> movimentosEntrada = movimentoRepository.findByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo)
                .stream()
                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                .collect(Collectors.toList());

        logger.info("Gerando relatório para " + mes + "/" + ano);
        logger.info("Total de movimentos de entrada: " + movimentosEntrada.size());

        RelatorioEntradasDetalhadasDto relatorio = new RelatorioEntradasDetalhadasDto();
        relatorio.setMes(mes);
        relatorio.setAno(ano);

        // Processar entradas por sócio - estrutura agrupada
        Map<String, List<Movimento>> movimentosPorSocio = new HashMap<>();
        
        for (Movimento movimento : movimentosEntrada) {
            String nomeSocio = "Não especificado";
            String origemDestino = movimento.getOrigemDestino();
            
            if (origemDestino != null && origemDestino.startsWith("Recebimento Mensalidade Sócio: ")) {
                // Formato: "Recebimento Mensalidade Sócio: [Nome do Sócio] - [Nome da Rubrica]"
                String info = origemDestino.substring("Recebimento Mensalidade Sócio: ".length());
                String[] partes = info.split(" - ", 2);
                if (partes.length == 2) {
                    nomeSocio = partes[0];
                }
            } else if (origemDestino != null && !origemDestino.isEmpty()) {
                nomeSocio = origemDestino;
            }
            
            movimentosPorSocio.computeIfAbsent(nomeSocio, k -> new ArrayList<>()).add(movimento);
        }
        
        // Criar a estrutura de detalhes por sócio
        Map<String, List<RelatorioEntradasDetalhadasDto.EntradaSocioDetalheDto>> detalhesPorSocio = new HashMap<>();
        List<RelatorioEntradasDetalhadasDto.EntradaSocioDto> entradasPorSocio = new ArrayList<>();
        
        for (Map.Entry<String, List<Movimento>> entry : movimentosPorSocio.entrySet()) {
            String nomeSocio = entry.getKey();
            List<Movimento> movimentos = entry.getValue();
            
            // Calcular total e quantidade para o sócio
            BigDecimal valorTotal = movimentos.stream()
                    .map(Movimento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Long quantidade = (long) movimentos.size();
            
            // Criar entrada do sócio
            RelatorioEntradasDetalhadasDto.EntradaSocioDto entradaSocio = new RelatorioEntradasDetalhadasDto.EntradaSocioDto();
            entradaSocio.setNomeSocio(nomeSocio);
            entradaSocio.setValorTotal(valorTotal);
            entradaSocio.setQuantidade(quantidade);
            entradasPorSocio.add(entradaSocio);
            
            // Criar detalhes do sócio
            List<RelatorioEntradasDetalhadasDto.EntradaSocioDetalheDto> detalhes = movimentos.stream()
                    .map(movimento -> {
                        RelatorioEntradasDetalhadasDto.EntradaSocioDetalheDto detalhe = new RelatorioEntradasDetalhadasDto.EntradaSocioDetalheDto();
                        detalhe.setDataCredito(movimento.getDataHora().toLocalDate());
                        detalhe.setValor(movimento.getValor());
                        
                        String rubrica = "Não especificada";
                        String tipoEntrada = "OUTROS"; // Valor padrão
                        Long cobrancaId = null;
                        
                        if (movimento.getOrigemDestino() != null && movimento.getOrigemDestino().startsWith("Recebimento Mensalidade Sócio: ")) {
                            tipoEntrada = "MENSALIDADE";
                            String info = movimento.getOrigemDestino().substring("Recebimento Mensalidade Sócio: ".length());
                            String[] partes = info.split(" - ", 2);
                            if (partes.length == 2) {
                                rubrica = partes[1];
                            }
                        } else if (movimento.getOrigemDestino() != null && movimento.getOrigemDestino().startsWith("Recebimento de mensalidade: ")) {
                            tipoEntrada = "MENSALIDADE";
                            rubrica = movimento.getRubrica() != null ? movimento.getRubrica().getNome() : "Não especificada";
                        } else if (movimento.getOrigemDestino() != null && movimento.getOrigemDestino().startsWith("Recebimento de outras rubricas: ")) {
                            tipoEntrada = "OUTRAS_RUBRICAS";
                            rubrica = movimento.getRubrica() != null ? movimento.getRubrica().getNome() : "Não especificada";
                        } else if (movimento.getOrigemDestino() != null && movimento.getOrigemDestino().startsWith("Recebimento de cobrança: ")) {
                            tipoEntrada = "COBRANCA";
                            rubrica = movimento.getRubrica() != null ? movimento.getRubrica().getNome() : "Não especificada";
                        } else if (movimento.getRubrica() != null) {
                            rubrica = movimento.getRubrica().getNome();
                        }
                        
                        detalhe.setRubrica(rubrica);
                        detalhe.setTipoEntrada(tipoEntrada);
                        // Se tivermos acesso ao ID da cobrança, podemos adicioná-lo aqui
                        // detalhe.setCobrancaId(cobrancaId);
                        
                        return detalhe;
                    })
                    .collect(Collectors.toList());
            
            detalhesPorSocio.put(nomeSocio, detalhes);
        }
        
        relatorio.setEntradasPorSocio(entradasPorSocio);
        relatorio.setDetalhesPorSocio(detalhesPorSocio);

        // Processar rubricas de pagamento (mantendo a estrutura original)
        Map<String, List<Movimento>> movimentosPorRubrica = movimentosEntrada.stream()
                .collect(Collectors.groupingBy(m -> {
                    if (m.getRubrica() != null) {
                        return m.getRubrica().getNome();
                    }
                    return "Não especificada";
                }));

        List<RelatorioEntradasDetalhadasDto.RubricaPagamentoDto> rubricasPagamento = movimentosPorRubrica.entrySet()
                .stream()
                .map(entry -> {
                    String nomeRubrica = entry.getKey();
                    List<Movimento> movimentos = entry.getValue();
                    BigDecimal valorTotal = movimentos.stream()
                            .map(Movimento::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long quantidade = (long) movimentos.size();

                    RelatorioEntradasDetalhadasDto.RubricaPagamentoDto rubricaDto = new RelatorioEntradasDetalhadasDto.RubricaPagamentoDto();
                    rubricaDto.setNomeRubrica(nomeRubrica);
                    rubricaDto.setValorTotal(valorTotal);
                    rubricaDto.setQuantidade(quantidade);
                    return rubricaDto;
                })
                .collect(Collectors.toList());

        relatorio.setRubricasPagamento(rubricasPagamento);

        // Calcular total de entradas
        BigDecimal totalEntradas = movimentosEntrada.stream()
                .map(Movimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        relatorio.setTotalEntradas(totalEntradas);

        logger.info("Total de sócios: " + entradasPorSocio.size());
        logger.info("Total de rubricas de pagamento: " + rubricasPagamento.size());
        logger.info("Valor total das entradas: " + totalEntradas);

        return relatorio;
    }
}
