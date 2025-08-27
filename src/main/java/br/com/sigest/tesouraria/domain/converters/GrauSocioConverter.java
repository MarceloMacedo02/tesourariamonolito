package br.com.sigest.tesouraria.domain.converters;

import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class GrauSocioConverter implements AttributeConverter<String, Integer> {
    @Override
    public Integer convertToDatabaseColumn(String grau) {
        try {
            return GrauSocio.fromCodigo(grau);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(Integer codigo) {
        try {
            return GrauSocio.fromDescricao(codigo);
        } catch (Exception e) {

        }
        return null;

    }

}