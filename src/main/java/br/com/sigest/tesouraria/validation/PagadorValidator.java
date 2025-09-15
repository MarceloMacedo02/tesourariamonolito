package br.com.sigest.tesouraria.validation;

import br.com.sigest.tesouraria.dto.ContaReceberDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PagadorValidator implements ConstraintValidator<ValidPagador, ContaReceberDto> {

    @Override
    public void initialize(ValidPagador constraintAnnotation) {
    }

    @Override
    public boolean isValid(ContaReceberDto dto, ConstraintValidatorContext context) {
        // Either socioId is provided or pagador is not blank
        return (dto.getSocioId() != null) || (dto.getPagador() != null && !dto.getPagador().trim().isEmpty());
    }
}