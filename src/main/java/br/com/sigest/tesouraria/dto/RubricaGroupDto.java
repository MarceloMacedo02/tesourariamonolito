package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RubricaGroupDto {
    private String nomeRubrica;
    private BigDecimal total;
}
