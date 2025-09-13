package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoProcessingResult {
    private List<TransacaoDto> creditTransacoes;
    private List<TransacaoDto> debitTransacoes;
}
