package br.com.sigest.tesouraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidadeRubrica;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.CobrancaRepository;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.repository.MovimentoRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;

@Service
public class CobrancaService {
    @Autowired
    private CobrancaRepository repository;
    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired
    private MovimentoRepository movimentoRepository;

    public Cobranca gerarCobrancaManual(Cobranca cobranca) {
        Socio socio = socioRepository.findById(cobranca.getSocio().getId())
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
        if (socio.getStatus() != StatusSocio.FREQUENTE) {
            throw new RegraNegocioException("Só é possível gerar cobranças para sócios com status 'FREQUENTE'.");
        }
        cobranca.setStatus(StatusCobranca.ABERTA);
        return repository.save(cobranca);
    }

    @Transactional
    public void registrarRecebimento(Long cobrancaId, PagamentoRequestDto pagamentoDto) {
        Cobranca cobranca = repository.findById(cobrancaId)
                .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada."));
        if (cobranca.getStatus() == StatusCobranca.PAGA || cobranca.getStatus() == StatusCobranca.CANCELADA) {
            throw new RegraNegocioException("Esta cobrança já foi paga ou cancelada.");
        }
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(pagamentoDto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));
        contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() + cobranca.getValor());
        contaFinanceiraRepository.save(contaFinanceira);
        cobranca.setStatus(StatusCobranca.PAGA);
        cobranca.setDataPagamento(pagamentoDto.getDataPagamento());
        repository.save(cobranca);
        GrupoMensalidade grupo = cobranca.getSocio().getGrupoMensalidade();
        if (grupo != null && grupo.getRubricas() != null && !grupo.getRubricas().isEmpty()) {
            float valorPorRubrica = cobranca.getValor() / grupo.getRubricas().size();
            // Arredondando para 2 casas decimais
            valorPorRubrica = Math.round(valorPorRubrica * 100.0f) / 100.0f;

            for (GrupoMensalidadeRubrica grupoMensalidadeRubrica : grupo.getRubricas()) {
                Movimento movimento = new Movimento();
                movimento.setTipo(TipoMovimento.CREDITO);
                movimento.setValor(valorPorRubrica);
                movimento.setContaFinanceira(contaFinanceira);
                movimento.setRubrica(grupoMensalidadeRubrica.getRubrica());
                movimento.setCentroCusto(grupoMensalidadeRubrica.getRubrica().getCentroCusto());
                movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
                movimento.setOrigemDestino("Recebimento Sócio: " + cobranca.getSocio().getNome()); // Assuming 'rubrica'
                                                                                                   // is still available
                                                                                                   // or needs to be
                                                                                                   // derived from
                                                                                                   // 'grupoMensalidadeRubrica'
                movimentoRepository.save(movimento);
            }
        }
    }
}