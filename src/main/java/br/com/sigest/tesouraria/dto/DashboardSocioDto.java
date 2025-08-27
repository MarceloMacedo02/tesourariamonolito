package br.com.sigest.tesouraria.dto;
import br.com.sigest.tesouraria.domain.entity.Cobranca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor
public class DashboardSocioDto {
    private List<Cobranca> cobrancasPendentes;
}