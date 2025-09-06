package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoProcessingResult {
    private List<TransacaoDto> creditTransacoes;
    private List<TransacaoDto> debitTransacoes;
}
