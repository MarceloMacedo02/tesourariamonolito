package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PagamentoLoteRequestDto {
    private List<Long> cobrancaIds;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPagamento;

    private Long contaFinanceiraId;

    private Double valorTotal;
    private Long transacaoId;
}