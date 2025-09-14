package br.com.sigest.tesouraria.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.repository.CobrancaRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.service.CobrancaService;
import lombok.extern.slf4j.Slf4j;

/**
 * Job de faturamento agendado para gerar cobranças mensais e atualizar status.
 */
@Slf4j
@Component
public class FaturamentoJob {

    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private CobrancaRepository cobrancaRepository;
    @Autowired
    private CobrancaService cobrancaService;

    /**
     * Gera cobranças de mensalidade automaticamente no dia 1 de cada mês.
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    public void gerarFaturamentoMensal() {
        log.info("Iniciando o job de faturamento mensal para gerar cobranças.");
        List<Socio> sociosAtivos = socioRepository.findByStatus(StatusSocio.FREQUENTE);
        for (Socio socio : sociosAtivos) {
            try {
                cobrancaService.gerarCobrancaMensalidade(socio);
            } catch (Exception e) {
                log.error("Erro ao gerar cobrança mensal para o sócio {}: {}", socio.getId(), e.getMessage());
            }
        }
        log.info("Job de faturamento mensal concluído.");
    }

    /**
     * Atualiza o status das cobranças vencidas diariamente.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void atualizarCobrancasVencidas() {
        log.info("Iniciando o job de atualização de cobranças vencidas.");
        List<Cobranca> cobrancasAbertas = cobrancaRepository
                .findByStatusAndDataVencimentoBefore(StatusCobranca.ABERTA, LocalDate.now());
        for (Cobranca cobranca : cobrancasAbertas) {
            cobranca.setStatus(StatusCobranca.VENCIDA);
            cobrancaRepository.save(cobranca);
        }
        log.info("Job de atualização de cobranças vencidas concluído. Total de cobranças atualizadas: {}",
                cobrancasAbertas.size());
    }
}