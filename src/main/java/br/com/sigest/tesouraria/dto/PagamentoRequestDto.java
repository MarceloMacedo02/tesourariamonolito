package br.com.sigest.tesouraria.dto;
import lombok.Data;
import java.time.LocalDate;
@Data
public class PagamentoRequestDto {
    private Long contaFinanceiraId;
    private LocalDate dataPagamento;
}