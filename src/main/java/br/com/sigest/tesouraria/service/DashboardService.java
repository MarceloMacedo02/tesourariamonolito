package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sigest.tesouraria.domain.entity.Usuario;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.dto.DashboardSocioDto;
import br.com.sigest.tesouraria.dto.DashboardTesoureiroDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.CobrancaRepository;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.repository.ContaPagarRepository;

@Service
public class DashboardService {
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired
    private CobrancaRepository cobrancaRepository;
    @Autowired
    private ContaPagarRepository contaPagarRepository;

    public DashboardTesoureiroDto getDashboardTesoureiro() {
        BigDecimal saldoConsolidado = contaFinanceiraRepository.sumTotalSaldo();
        BigDecimal totalAReceber = cobrancaRepository.sumTotalAReceber();
        BigDecimal totalAPagar = contaPagarRepository.sumTotalAPagar();

        // Lógica para calcular o resultado do mês (exemplo)
        // BigDecimal resultadoDoMes = movimentacaoRepository.getResultadoDoMes();
        BigDecimal resultadoDoMes = BigDecimal.valueOf(40000); // Valor de exemplo

        return new DashboardTesoureiroDto(
                saldoConsolidado != null ? saldoConsolidado : BigDecimal.ZERO,
                totalAReceber != null ? totalAReceber : BigDecimal.ZERO,
                totalAPagar != null ? totalAPagar : BigDecimal.ZERO,
                resultadoDoMes != null ? resultadoDoMes : BigDecimal.ZERO // Garante que nunca seja nulo
        );
    }

    public DashboardSocioDto getDashboardSocio(Usuario usuario) {
        if (usuario.getSocio() == null) {
            throw new RegraNegocioException("Este usuário não está associado a um sócio.");
        }
        DashboardSocioDto dto = new DashboardSocioDto();
        dto.setCobrancasPendentes(
                cobrancaRepository.findBySocioIdAndStatus(usuario.getSocio().getId(), StatusCobranca.ABERTA));
        dto.getCobrancasPendentes()
                .addAll(cobrancaRepository.findBySocioIdAndStatus(usuario.getSocio().getId(), StatusCobranca.VENCIDA));
        return dto;
    }
}