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
}