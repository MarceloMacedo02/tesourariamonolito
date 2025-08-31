package br.com.sigest.tesouraria.service;
import br.com.sigest.tesouraria.domain.entity.*;
import br.com.sigest.tesouraria.domain.enums.*;
import br.com.sigest.tesouraria.dto.*;
import br.com.sigest.tesouraria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class RelatorioService {
    @Autowired private MovimentoRepository movimentoRepository;
    @Autowired private CobrancaRepository cobrancaRepository;
    @Autowired private SocioRepository socioRepository;
    @Autowired private ContaPagarRepository contaPagarRepository;
    @Autowired private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired private ReconciliacaoMensalRepository reconciliacaoMensalRepository;
    public RelatorioBalanceteDto gerarBalancete(LocalDate inicio, LocalDate fim) {
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();
        BigDecimal receitas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.CREDITO, inicioDt, fimDt);
        BigDecimal despesas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.DEBITO, inicioDt, fimDt);
        receitas = receitas == null ? BigDecimal.ZERO : receitas;
        despesas = despesas == null ? BigDecimal.ZERO : despesas;
        BigDecimal resultado = receitas.subtract(despesas);
        return new RelatorioBalanceteDto(receitas, despesas, resultado);
    }
    public List<SocioInadimplenteDto> gerarListaInadimplentes() {
        List<Socio> socios = socioRepository.findSociosInadimplentes();
        return socios.stream().map(socio -> {
            List<Cobranca> cobrancas = cobrancaRepository.findBySocioIdAndStatus(socio.getId(), StatusCobranca.VENCIDA);
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
                                Collectors.reducing(BigDecimal.ZERO, mov -> BigDecimal.valueOf(mov.getValor()), BigDecimal::add)
                        )
                ));

        for (java.util.Map.Entry<TipoRubrica, java.util.Map<String, BigDecimal>> entry : groupedMovimentos.entrySet()) {
            TipoRubrica tipoRubrica = entry.getKey();
            java.util.Map<String, BigDecimal> rubricasMap = entry.getValue();

            List<RelatorioDemonstrativoFinanceiroDto.RubricaDetalheDto> rubricasDetalhe = rubricasMap.entrySet().stream()
                    .map(e -> new RelatorioDemonstrativoFinanceiroDto.RubricaDetalheDto(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            BigDecimal totalCategoria = rubricasMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto rubricaAgrupadaDto =
                    new RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto(tipoRubrica, rubricasDetalhe, totalCategoria);

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


        return new RelatorioDemonstrativoFinanceiroDto(
                mes,
                ano,
                saldoPeriodoAnterior,
                totalEntradas,
                totalSaidas,
                saldoOperacional,
                saldoFinalCaixaBanco,
                entradasAgrupadas,
                saidasAgrupadas
        );
    }
}