package br.com.sigest.tesouraria.dto;

import java.util.List;

public class TransacaoPagamentoRequestDto {
    private List<Long> cobrancaIds;
    private Long contaFinanceiraId;

    public List<Long> getCobrancaIds() {
        return cobrancaIds;
    }

    public void setCobrancaIds(List<Long> cobrancaIds) {
        this.cobrancaIds = cobrancaIds;
    }

    public Long getContaFinanceiraId() {
        return contaFinanceiraId;
    }

    public void setContaFinanceiraId(Long contaFinanceiraId) {
        this.contaFinanceiraId = contaFinanceiraId;
    }
}