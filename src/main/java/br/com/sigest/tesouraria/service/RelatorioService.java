package br.com.sigest.tesouraria.service;

import br.com.sigest.tesouraria.domain.entity.GrupoRubrica;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.dto.RelatorioFinanceiroGruposRubricaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private MovimentoRepository movimentoRepository;

    public RelatorioFinanceiroGruposRubricaDto gerarRelatorioFinanceiroGruposRubrica(Integer mes, Integer ano) {
        RelatorioFinanceiroGruposRubricaDto relatorio = new RelatorioFinanceiroGruposRubricaDto();
        relatorio.setMes(mes);
        relatorio.setAno(ano);

        // Definir o período de busca
        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fim = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Buscar movimentos do período
        List<Movimento> movimentos = movimentoRepository.findByDataHoraBetween(inicio, fim);

        // Calcular totais gerais
        BigDecimal totalEntradas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                .map(Movimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                .map(Movimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        relatorio.setTotalEntradas(totalEntradas);
        relatorio.setTotalSaidas(totalSaidas);
        relatorio.setSaldoOperacional(totalEntradas.subtract(totalSaidas));

        // Agrupar movimentos por grupo de rubrica
        Map<GrupoRubrica, List<Movimento>> movimentosPorGrupo = movimentos.stream()
                .filter(m -> m.getGrupoRubrica() != null)
                .collect(Collectors.groupingBy(Movimento::getGrupoRubrica));

        // Converter para DTO
        List<RelatorioFinanceiroGruposRubricaDto.GrupoRubricaDto> gruposDto = new ArrayList<>();
        
        for (Map.Entry<GrupoRubrica, List<Movimento>> entry : movimentosPorGrupo.entrySet()) {
            GrupoRubrica grupo = entry.getKey();
            List<Movimento> movimentosDoGrupo = entry.getValue();
            
            RelatorioFinanceiroGruposRubricaDto.GrupoRubricaDto grupoDto = new RelatorioFinanceiroGruposRubricaDto.GrupoRubricaDto();
            grupoDto.setIdGrupoRubrica(grupo.getId());
            grupoDto.setNomeGrupoRubrica(grupo.getNome());
            
            // Calcular totais do grupo
            BigDecimal entradasGrupo = movimentosDoGrupo.stream()
                    .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                    .map(Movimento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal saidasGrupo = movimentosDoGrupo.stream()
                    .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                    .map(Movimento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            grupoDto.setTotalEntradas(entradasGrupo);
            grupoDto.setTotalSaidas(saidasGrupo);
            grupoDto.setSaldo(entradasGrupo.subtract(saidasGrupo));
            
            // Converter movimentos para DTO
            List<RelatorioFinanceiroGruposRubricaDto.MovimentoDto> movimentosDto = movimentosDoGrupo.stream()
                    .map(this::converterParaMovimentoDto)
                    .collect(Collectors.toList());
            
            grupoDto.setMovimentos(movimentosDto);
            gruposDto.add(grupoDto);
        }
        
        // Ordenar grupos por nome
        gruposDto.sort((g1, g2) -> g1.getNomeGrupoRubrica().compareTo(g2.getNomeGrupoRubrica()));
        
        relatorio.setGruposRubricaAgrupados(gruposDto);
        
        return relatorio;
    }

    private RelatorioFinanceiroGruposRubricaDto.MovimentoDto converterParaMovimentoDto(Movimento movimento) {
        RelatorioFinanceiroGruposRubricaDto.MovimentoDto dto = new RelatorioFinanceiroGruposRubricaDto.MovimentoDto();
        dto.setId(movimento.getId());
        dto.setDescricao(movimento.getOrigemDestino());
        dto.setTipoRubrica(movimento.getRubrica() != null ? movimento.getRubrica().getTipo() : null);
        dto.setNomeRubrica(movimento.getRubrica() != null ? movimento.getRubrica().getNome() : "Não especificado");
        dto.setOrigemDestino(movimento.getOrigemDestino());
        dto.setValor(movimento.getValor());
        dto.setData(movimento.getDataHora());
        return dto;
    }
    
    public java.io.ByteArrayInputStream gerarRelatorioFinanceiroGruposRubricaPdf(RelatorioFinanceiroGruposRubricaDto relatorio) {
        // Este método será implementado posteriormente se necessário
        // Por enquanto, retornamos um stream vazio
        return new java.io.ByteArrayInputStream(new byte[0]);
    }
}