package br.com.sigest.tesouraria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Transacao;
import br.com.sigest.tesouraria.domain.entity.TransacaoPendente;
import br.com.sigest.tesouraria.domain.enums.StatusIdentificacao;
import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.domain.repository.TransacaoPendenteRepository;
import br.com.sigest.tesouraria.domain.repository.TransacaoRepository;
import br.com.sigest.tesouraria.exception.RegraNegocioException;

@Service
public class TransacaoPendenteService {

    @Autowired
    private TransacaoPendenteRepository transacaoPendenteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private SocioRepository socioRepository;

    /**
     * Busca todas as transações pendentes
     */
    public List<TransacaoPendente> findAllPendentes() {
        return transacaoPendenteRepository.findByProcessadoFalseOrderByDataImportacaoDesc();
    }

    /**
     * Busca uma transação pendente por ID
     */
    public TransacaoPendente findById(Long id) {
        return transacaoPendenteRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Transação pendente não encontrada."));
    }

    /**
     * Conta quantas transações pendentes existem
     */
    public long countPendentes() {
        return transacaoPendenteRepository.countPendentes();
    }

    /**
     * Associa uma transação pendente a um sócio e cria a transação definitiva
     */
    @Transactional
    public void associarSocio(Long transacaoPendenteId, Long socioId) {
        TransacaoPendente transacaoPendente = findById(transacaoPendenteId);
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));

        // Verificar se a transação já existe no banco
        Transacao existingTransacao = transacaoRepository.findByDataAndTipoAndValorAndDescricaoAndDocumento(
                transacaoPendente.getData(),
                transacaoPendente.getTipo(),
                transacaoPendente.getValor(),
                transacaoPendente.getDescricao(),
                transacaoPendente.getDocumento()).orElse(null);

        if (existingTransacao != null) {
            throw new RegraNegocioException("Já existe uma transação com esses dados no sistema.");
        }

        // Criar nova transação
        Transacao novaTransacao = new Transacao();
        novaTransacao.setData(transacaoPendente.getData());
        novaTransacao.setTipo(transacaoPendente.getTipo());
        novaTransacao.setValor(transacaoPendente.getValor());
        novaTransacao.setDescricao(transacaoPendente.getDescricao());
        novaTransacao.setDocumento(socio.getCpf()); // Usar o CPF do sócio
        novaTransacao.setFornecedorOuSocio(socio.getNome()); // Usar o nome do sócio
        novaTransacao.setRelacionadoId(socio.getId());
        novaTransacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
        novaTransacao.setStatusIdentificacao(StatusIdentificacao.IDENTIFICADO);

        // Salvar a transação
        transacaoRepository.save(novaTransacao);

        // Marcar a transação pendente como processada
        transacaoPendente.setProcessado(true);
        transacaoPendenteRepository.save(transacaoPendente);
    }

    /**
     * Descarta uma transação pendente (marca como processada sem criar transação)
     */
    @Transactional
    public void descartarTransacao(Long transacaoPendenteId) {
        TransacaoPendente transacaoPendente = findById(transacaoPendenteId);
        transacaoPendente.setProcessado(true);
        transacaoPendenteRepository.save(transacaoPendente);
    }

    /**
     * Remove definitivamente uma transação pendente
     */
    @Transactional
    public void removerTransacao(Long transacaoPendenteId) {
        TransacaoPendente transacaoPendente = findById(transacaoPendenteId);
        transacaoPendenteRepository.delete(transacaoPendente);
    }
}