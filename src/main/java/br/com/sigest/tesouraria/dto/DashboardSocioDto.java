package br.com.sigest.tesouraria.dto;
import br.com.sigest.tesouraria.domain.entity.Cobranca;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DashboardSocioDto {
    private List<Cobranca> cobrancasPendentes;
}