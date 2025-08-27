package br.com.sigest.tesouraria.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidadeRubrica;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.repository.CobrancaRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;

@Component
public class FaturamentoJob {
    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private CobrancaRepository cobrancaRepository;

    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    public void gerarFaturamentoMensal() {
        List<Socio> sociosAtivos = socioRepository.findByStatus(StatusSocio.FREQUENTE);
        for (Socio socio : sociosAtivos) {
            if (socio.getGrupoMensalidade() != null && !socio.getGrupoMensalidade().getRubricas().isEmpty()) {
                // Calcular o valor total somando os valores padrão das rubricas
                float valorTotal = 0.0F;
                for (GrupoMensalidadeRubrica rubrica : socio.getGrupoMensalidade().getRubricas()) {
                    valorTotal += rubrica.getRubrica().getValorPadrao();
                }

                Cobranca cobranca = new Cobranca();
                cobranca.setSocio(socio);
                cobranca.setValor(valorTotal);
                cobranca.setDataVencimento(LocalDate.now().withDayOfMonth(10));
                cobranca.setStatus(StatusCobranca.ABERTA);
                cobrancaRepository.save(cobranca);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void atualizarCobrancasVencidas() {
        List<Cobranca> cobrancasAbertas = cobrancaRepository
                .findByStatusAndDataVencimentoBefore(StatusCobranca.ABERTA, LocalDate.now());
        for (Cobranca cobranca : cobrancasAbertas) {
            cobranca.setStatus(StatusCobranca.VENCIDA);
            cobrancaRepository.save(cobranca);
        }
    }
}