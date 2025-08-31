package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RubricaGroupDto {
    private String nomeRubrica;
    private BigDecimal total;
}
