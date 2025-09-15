package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import br.com.sigest.tesouraria.dto.RelatorioFluxoCaixaDto;
import br.com.sigest.tesouraria.dto.SocioInadimplenteDto;

@Service
public class RelatorioService {
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

    public RelatorioDemonstrativoFinanceiroDto gerarDemonstrativoFinanceiro(int mes, int ano) {
        // Saldo do período anterior
        int mesAnterior = mes == 1 ? 12 : mes - 1;
        int anoAnterior = mes == 1 ? ano - 1 : ano;

        BigDecimal saldoPeriodoAnterior = reconciliacaoMensalRepository
                .findByMesAndAno(mesAnterior, anoAnterior)
                .stream()
                .map(ReconciliacaoMensal::getSaldoFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Entradas e Saídas do período
        LocalDateTime inicioDoPeriodo = LocalDate.of(ano, mes, 1).atStartOfDay();
        LocalDateTime fimDoPeriodo = LocalDate.of(ano, mes, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59);

        List<Movimento> movimentosDoPeriodo = movimentoRepository.findByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo);

        BigDecimal totalEntradas = BigDecimal.ZERO;
        BigDecimal totalSaidas = BigDecimal.ZERO;

        List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> entradasAgrupadas = new java.util.ArrayList<>();
        List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> saidasAgrupadas = new java.util.ArrayList<>();

        // Group movements by rubrica type (RECEITA/DESPESA) and then by rubrica name
        java.util.Map<TipoRubrica, java.util.Map<String, BigDecimal>> groupedMovimentos = movimentosDoPeriodo.stream()
                .collect(Collectors.groupingBy(
                        mov -> mov.getRubrica().getTipo(),
                        Collectors.groupingBy(
                                mov -> mov.getRubrica().getNome(),
                                // Usando mov.getValor() diretamente em vez de BigDecimal.valueOf(mov.getValor())
                                // porque mov.getValor() já retorna um BigDecimal, evitando conversões desnecessárias
                                Collectors.reducing(BigDecimal.ZERO, mov -> mov.getValor(),
                                        BigDecimal::add))));

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
                totalEntradas = totalEntradas.add(totalCategoria);
            } else {
                saidasAgrupadas.add(rubricaAgrupadaDto);
                totalSaidas = totalSaidas.add(totalCategoria);
            }
        }

        // Saldo Operacional
        BigDecimal saldoOperacional = saldoPeriodoAnterior.add(totalEntradas).subtract(totalSaidas);

        // Saldo Final em Caixa e Banco
        BigDecimal saldoFinalCaixaBanco = contaFinanceiraRepository.sumTotalSaldo();
        saldoFinalCaixaBanco = saldoFinalCaixaBanco == null ? BigDecimal.ZERO : saldoFinalCaixaBanco;

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
}
