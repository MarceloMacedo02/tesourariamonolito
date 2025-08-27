package br.com.sigest.tesouraria.dto;
import br.com.sigest.tesouraria.domain.entity.Cobranca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor
public class SocioInadimplenteDto {
    private Long socioId;
    private String nomeSocio;
    private List<Cobranca> cobrancasVencidas;
}