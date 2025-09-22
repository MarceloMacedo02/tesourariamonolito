package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sigest.tesouraria.domain.entity.GrupoRubrica;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.ReconciliacaoMensalRepository;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroDto;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto;
import br.com.sigest.tesouraria.dto.RelatorioEntradasDetalhadasDto;
import br.com.sigest.tesouraria.dto.RelatorioFinanceiroGruposRubricaDto;

@Service
public class RelatorioService {

        @Autowired
        private MovimentoRepository movimentoRepository;

        @Autowired
        private ReconciliacaoMensalRepository reconciliacaoMensalRepository;

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

                // Buscar o saldo do período anterior na reconciliação mensal
                BigDecimal saldoPeriodoAnterior = BigDecimal.ZERO;
                List<ReconciliacaoMensal> reconciliacoes = reconciliacaoMensalRepository.findByMesAndAno(mes, ano);
                if (!reconciliacoes.isEmpty()) {
                        // Se houver múltiplas reconciliações para o mesmo mês/ano, somamos todas
                        saldoPeriodoAnterior = reconciliacoes.stream()
                                        .map(ReconciliacaoMensal::getSaldoInicial)
                                        .filter(saldo -> saldo != null)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                relatorio.setSaldoPeriodoAnterior(saldoPeriodoAnterior);

                // Calcular o saldo final em caixa e banco
                BigDecimal saldoFinalCaixaBanco = saldoPeriodoAnterior.add(totalEntradas).subtract(totalSaidas);
                relatorio.setSaldoFinalCaixaBanco(saldoFinalCaixaBanco);

                // Separar movimentos por tipo
                List<Movimento> movimentosEntrada = movimentos.stream()
                                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                                .collect(Collectors.toList());

                List<Movimento> movimentosSaida = movimentos.stream()
                                .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                                .collect(Collectors.toList());

                // Processar grupos de rubrica para ENTRADAS
                List<RelatorioFinanceiroGruposRubricaDto.GrupoRubricaDto> gruposEntrada = processarGruposRubrica(
                                movimentosEntrada, TipoMovimento.ENTRADA);

                // Processar grupos de rubrica para SAÍDAS
                List<RelatorioFinanceiroGruposRubricaDto.GrupoRubricaDto> gruposSaida = processarGruposRubrica(
                                movimentosSaida,
                                TipoMovimento.SAIDA);

                relatorio.setGruposRubricaEntrada(gruposEntrada);
                relatorio.setGruposRubricaSaida(gruposSaida);

                return relatorio;
        }

        private List<RelatorioFinanceiroGruposRubricaDto.GrupoRubricaDto> processarGruposRubrica(
                        List<Movimento> movimentos,
                        TipoMovimento tipo) {
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
                        BigDecimal totalGrupo = movimentosDoGrupo.stream()
                                        .map(Movimento::getValor)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        if (tipo == TipoMovimento.ENTRADA) {
                                grupoDto.setTotalEntradas(totalGrupo);
                                grupoDto.setTotalSaidas(BigDecimal.ZERO);
                        } else {
                                grupoDto.setTotalEntradas(BigDecimal.ZERO);
                                grupoDto.setTotalSaidas(totalGrupo);
                        }
                        grupoDto.setSaldo(totalGrupo);

                        // Agrupar movimentos por rubrica
                        Map<Rubrica, List<Movimento>> movimentosPorRubrica = movimentosDoGrupo.stream()
                                        .filter(m -> m.getRubrica() != null)
                                        .collect(Collectors.groupingBy(Movimento::getRubrica));

                        // Converter rubricas para DTO
                        List<RelatorioFinanceiroGruposRubricaDto.RubricaDto> rubricasDto = new ArrayList<>();
                        for (Map.Entry<Rubrica, List<Movimento>> rubricaEntry : movimentosPorRubrica.entrySet()) {
                                Rubrica rubrica = rubricaEntry.getKey();
                                List<Movimento> movimentosDaRubrica = rubricaEntry.getValue();

                                RelatorioFinanceiroGruposRubricaDto.RubricaDto rubricaDto = new RelatorioFinanceiroGruposRubricaDto.RubricaDto();
                                rubricaDto.setNomeRubrica(rubrica.getNome());

                                // Calcular total da rubrica
                                BigDecimal totalRubrica = movimentosDaRubrica.stream()
                                                .map(Movimento::getValor)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                rubricaDto.setTotalValor(totalRubrica);

                                if (tipo == TipoMovimento.ENTRADA) {
                                        rubricaDto.setTotalEntradas(totalRubrica);
                                        rubricaDto.setTotalSaidas(BigDecimal.ZERO);
                                        // Separar movimentos de entrada
                                        List<RelatorioFinanceiroGruposRubricaDto.MovimentoDto> movimentosEntradaDto = movimentosDaRubrica
                                                        .stream()
                                                        .map(this::converterParaMovimentoDto)
                                                        .collect(Collectors.toList());
                                        rubricaDto.setMovimentosEntrada(movimentosEntradaDto);
                                        rubricaDto.setMovimentosSaida(new ArrayList<>());
                                } else {
                                        rubricaDto.setTotalEntradas(BigDecimal.ZERO);
                                        rubricaDto.setTotalSaidas(totalRubrica);
                                        // Separar movimentos de saída
                                        List<RelatorioFinanceiroGruposRubricaDto.MovimentoDto> movimentosSaidaDto = movimentosDaRubrica
                                                        .stream()
                                                        .map(this::converterParaMovimentoDto)
                                                        .collect(Collectors.toList());
                                        rubricaDto.setMovimentosSaida(movimentosSaidaDto);
                                        rubricaDto.setMovimentosEntrada(new ArrayList<>());
                                }

                                rubricasDto.add(rubricaDto);
                        }

                        // Ordenar rubricas por nome
                        rubricasDto.sort((r1, r2) -> r1.getNomeRubrica().compareTo(r2.getNomeRubrica()));

                        grupoDto.setRubricas(rubricasDto);
                        gruposDto.add(grupoDto);
                }

                // Ordenar grupos por nome
                gruposDto.sort((g1, g2) -> g1.getNomeGrupoRubrica().compareTo(g2.getNomeGrupoRubrica()));

                return gruposDto;
        }

        private RelatorioFinanceiroGruposRubricaDto.MovimentoDto converterParaMovimentoDto(Movimento movimento) {
                RelatorioFinanceiroGruposRubricaDto.MovimentoDto dto = new RelatorioFinanceiroGruposRubricaDto.MovimentoDto();
                dto.setId(movimento.getId());
                dto.setDescricao(movimento.getOrigemDestino());
                dto.setTipoRubrica(movimento.getRubrica() != null ? movimento.getRubrica().getTipo() : null);
                dto.setNomeRubrica(
                                movimento.getRubrica() != null ? movimento.getRubrica().getNome() : "Não especificado");
                dto.setOrigemDestino(movimento.getOrigemDestino());
                dto.setValor(movimento.getValor());
                dto.setData(movimento.getDataHora());
                return dto;
        }

        public RelatorioDemonstrativoFinanceiroDto gerarDemonstrativoFinanceiro(Integer mes, Integer ano) {
                RelatorioDemonstrativoFinanceiroDto relatorio = new RelatorioDemonstrativoFinanceiroDto();
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

                // Buscar o saldo do período anterior na reconciliação mensal
                BigDecimal saldoPeriodoAnterior = BigDecimal.ZERO;
                List<ReconciliacaoMensal> reconciliacoes = reconciliacaoMensalRepository.findByMesAndAno(mes, ano);
                if (!reconciliacoes.isEmpty()) {
                        saldoPeriodoAnterior = reconciliacoes.stream()
                                        .map(ReconciliacaoMensal::getSaldoInicial)
                                        .filter(saldo -> saldo != null)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                relatorio.setSaldoPeriodoAnterior(saldoPeriodoAnterior);

                // Calcular o saldo final em caixa e banco
                BigDecimal saldoFinalCaixaBanco = saldoPeriodoAnterior.add(totalEntradas).subtract(totalSaidas);
                relatorio.setSaldoFinalCaixaBanco(saldoFinalCaixaBanco);

                // Processar entradas agrupadas por tipo de rubrica
                List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> entradasAgrupadas = processarMovimentosPorTipoRubrica(
                                movimentos, TipoMovimento.ENTRADA);

                // Processar saídas agrupadas por tipo de rubrica
                List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> saidasAgrupadas = processarMovimentosPorTipoRubrica(
                                movimentos, TipoMovimento.SAIDA);

                relatorio.setEntradasAgrupadas(entradasAgrupadas);
                relatorio.setSaidasAgrupadas(saidasAgrupadas);

                return relatorio;
        }

        public RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto gerarDemonstrativoFinanceiroPorGrupoRubrica(
                        Integer mes, Integer ano) {
                RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto relatorio = new RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto();

                // Preencher dados básicos usando o método existente
                RelatorioDemonstrativoFinanceiroDto dadosBasicos = gerarDemonstrativoFinanceiro(mes, ano);
                relatorio.setMes(dadosBasicos.getMes());
                relatorio.setAno(dadosBasicos.getAno());
                relatorio.setSaldoPeriodoAnterior(dadosBasicos.getSaldoPeriodoAnterior());
                relatorio.setTotalEntradas(dadosBasicos.getTotalEntradas());
                relatorio.setTotalSaidas(dadosBasicos.getTotalSaidas());
                relatorio.setSaldoOperacional(dadosBasicos.getSaldoOperacional());
                relatorio.setSaldoFinalCaixaBanco(dadosBasicos.getSaldoFinalCaixaBanco());
                relatorio.setEntradasAgrupadas(dadosBasicos.getEntradasAgrupadas());
                relatorio.setSaidasAgrupadas(dadosBasicos.getSaidasAgrupadas());

                // Definir o período de busca
                YearMonth yearMonth = YearMonth.of(ano, mes);
                LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
                LocalDateTime fim = yearMonth.atEndOfMonth().atTime(23, 59, 59);

                // Buscar movimentos do período
                List<Movimento> movimentos = movimentoRepository.findByDataHoraBetween(inicio, fim);

                // Processar grupos de rubrica
                List<RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.GrupoRubricaAgrupadoDto> gruposAgrupados = processarMovimentosPorGrupoRubrica(
                                movimentos);

                relatorio.setGruposRubricaAgrupados(gruposAgrupados);

                return relatorio;
        }

        public RelatorioEntradasDetalhadasDto gerarRelatorioEntradasDetalhadas(Integer mes, Integer ano) {
                RelatorioEntradasDetalhadasDto relatorio = new RelatorioEntradasDetalhadasDto();
                relatorio.setMes(mes);
                relatorio.setAno(ano);

                // Definir o período de busca
                YearMonth yearMonth = YearMonth.of(ano, mes);
                LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
                LocalDateTime fim = yearMonth.atEndOfMonth().atTime(23, 59, 59);

                // Buscar apenas movimentos de entrada do período
                List<Movimento> movimentosEntrada = movimentoRepository.findByDataHoraBetween(inicio, fim)
                                .stream()
                                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                                .collect(Collectors.toList());

                // Calcular total de entradas
                BigDecimal totalEntradas = movimentosEntrada.stream()
                                .map(Movimento::getValor)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                relatorio.setTotalEntradas(totalEntradas);

                // Processar entradas por sócio (se houver informação de sócio)
                List<RelatorioEntradasDetalhadasDto.EntradaSocioDto> entradasPorSocio = new ArrayList<>();
                // TODO: Implementar agrupamento por sócio quando a entidade Socio estiver
                // disponível
                relatorio.setEntradasPorSocio(entradasPorSocio);

                // Processar rubricas de pagamento
                Map<String, List<Movimento>> movimentosPorRubrica = movimentosEntrada.stream()
                                .filter(m -> m.getRubrica() != null)
                                .collect(Collectors.groupingBy(m -> m.getRubrica().getNome()));

                List<RelatorioEntradasDetalhadasDto.RubricaPagamentoDto> rubricasPagamento = movimentosPorRubrica
                                .entrySet().stream()
                                .map(entry -> {
                                        RelatorioEntradasDetalhadasDto.RubricaPagamentoDto rubrica = new RelatorioEntradasDetalhadasDto.RubricaPagamentoDto();
                                        rubrica.setNomeRubrica(entry.getKey());
                                        rubrica.setQuantidade((long) entry.getValue().size());
                                        rubrica.setValorTotal(entry.getValue().stream()
                                                        .map(Movimento::getValor)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                                        return rubrica;
                                })
                                .collect(Collectors.toList());

                relatorio.setRubricasPagamento(rubricasPagamento);

                return relatorio;
        }

        private List<RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto> processarMovimentosPorTipoRubrica(
                        List<Movimento> movimentos, TipoMovimento tipoMovimento) {

                // Filtrar movimentos pelo tipo
                List<Movimento> movimentosFiltrados = movimentos.stream()
                                .filter(m -> m.getTipo() == tipoMovimento)
                                .collect(Collectors.toList());

                // Agrupar por tipo de rubrica
                Map<TipoRubrica, List<Movimento>> movimentosPorTipoRubrica = movimentosFiltrados.stream()
                                .filter(m -> m.getRubrica() != null)
                                .collect(Collectors.groupingBy(m -> m.getRubrica().getTipo()));

                // Converter para DTO
                return movimentosPorTipoRubrica.entrySet().stream()
                                .map(entry -> {
                                        TipoRubrica tipoRubrica = entry.getKey();
                                        List<Movimento> movimentosDoTipo = entry.getValue();

                                        // Agrupar por nome da rubrica
                                        Map<String, BigDecimal> rubricasMap = movimentosDoTipo.stream()
                                                        .collect(Collectors.groupingBy(
                                                                        m -> m.getRubrica().getNome(),
                                                                        Collectors.mapping(
                                                                                        Movimento::getValor,
                                                                                        Collectors.reducing(
                                                                                                        BigDecimal.ZERO,
                                                                                                        BigDecimal::add))));

                                        // Criar lista de detalhes
                                        List<RelatorioDemonstrativoFinanceiroDto.RubricaDetalheDto> rubricasDetalhe = rubricasMap
                                                        .entrySet().stream()
                                                        .map(e -> new RelatorioDemonstrativoFinanceiroDto.RubricaDetalheDto(
                                                                        e.getKey(), e.getValue()))
                                                        .collect(Collectors.toList());

                                        // Calcular total do tipo
                                        BigDecimal totalTipo = rubricasMap.values().stream()
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        return new RelatorioDemonstrativoFinanceiroDto.RubricaAgrupadaDto(
                                                        tipoRubrica, rubricasDetalhe, totalTipo);
                                })
                                .collect(Collectors.toList());
        }

        private List<RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.GrupoRubricaAgrupadoDto> processarMovimentosPorGrupoRubrica(
                        List<Movimento> movimentos) {

                // Agrupar movimentos por grupo de rubrica
                Map<GrupoRubrica, List<Movimento>> movimentosPorGrupo = movimentos.stream()
                                .filter(m -> m.getGrupoRubrica() != null)
                                .collect(Collectors.groupingBy(Movimento::getGrupoRubrica));

                // Converter para DTO
                return movimentosPorGrupo.entrySet().stream()
                                .map(entry -> {
                                        GrupoRubrica grupo = entry.getKey();
                                        List<Movimento> movimentosDoGrupo = entry.getValue();

                                        RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.GrupoRubricaAgrupadoDto grupoDto = new RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.GrupoRubricaAgrupadoDto();

                                        grupoDto.setIdGrupoRubrica(grupo.getId());
                                        grupoDto.setNomeGrupoRubrica(grupo.getNome());

                                        // Calcular totais por tipo
                                        BigDecimal totalEntradas = movimentosDoGrupo.stream()
                                                        .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                                                        .map(Movimento::getValor)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        BigDecimal totalSaidas = movimentosDoGrupo.stream()
                                                        .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                                                        .map(Movimento::getValor)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        grupoDto.setTotalEntradas(totalEntradas);
                                        grupoDto.setTotalSaidas(totalSaidas);
                                        grupoDto.setSaldo(totalEntradas.subtract(totalSaidas));

                                        // Converter movimentos para DTO
                                        List<RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.MovimentoDetalheDto> movimentosDto = movimentosDoGrupo
                                                        .stream()
                                                        .map(this::converterParaMovimentoDetalheDto)
                                                        .collect(Collectors.toList());

                                        grupoDto.setMovimentos(movimentosDto);

                                        return grupoDto;
                                })
                                .collect(Collectors.toList());
        }

        private RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.MovimentoDetalheDto converterParaMovimentoDetalheDto(
                        Movimento movimento) {
                RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.MovimentoDetalheDto dto = new RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.MovimentoDetalheDto();

                dto.setIdMovimento(movimento.getId());
                dto.setData(movimento.getDataHora());
                dto.setDescricao(movimento.getOrigemDestino());
                dto.setTipoRubrica(movimento.getRubrica() != null ? movimento.getRubrica().getTipo() : null);
                dto.setNomeRubrica(
                                movimento.getRubrica() != null ? movimento.getRubrica().getNome() : "Não especificado");
                dto.setValor(movimento.getValor());
                dto.setOrigemDestino(movimento.getOrigemDestino());

                return dto;
        }
}