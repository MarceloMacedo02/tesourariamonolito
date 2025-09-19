package br.com.sigest.tesouraria.domain.converters;

import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class GrauSocioConverter implements AttributeConverter<String, Integer> {
    @Override
    public Integer convertToDatabaseColumn(String grau) {
        if (grau == null) {
            return null;
        }
        
        try {
            return GrauSocio.getByDescricao(grau).getCodigo();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        
        try {
            // Percorrer todos os valores do enum para encontrar o que tem o código correspondente
            for (GrauSocio grau : GrauSocio.values()) {
                if (grau.getCodigo() == codigo) {
                    return grau.getDescricao();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}