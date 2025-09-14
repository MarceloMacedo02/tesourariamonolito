package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.CentroCusto;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.domain.repository.CentroCustoRepository;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;
import br.com.sigest.tesouraria.dto.DemonstrativoFinanceiroMensalDto;
import br.com.sigest.tesouraria.dto.ExtratoFiltroDto;
import br.com.sigest.tesouraria.dto.MovimentoDto;
import br.com.sigest.tesouraria.dto.RubricaGroupDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;

@Service
public class MovimentoService {

    @Autowired
    private MovimentoRepository movimentoRepository;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Autowired
    private RubricaRepository rubricaRepository;

    @Autowired
    private CentroCustoRepository centroCustoRepository;

    public List<Movimento> findAll() {
        return movimentoRepository.findAll();
    }

    public Movimento findById(Long id) {
        return movimentoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Movimento não encontrado."));
    }

    @Transactional
    public Movimento registrarMovimento(MovimentoDto dto) {
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(dto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));

        Rubrica rubrica = rubricaRepository.findById(dto.getRubricaId())
                .orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada."));

        // Validação da regra de negócio: Saída deve ser DESPESA, Entrada deve ser
        // RECEITA
        if (dto.getTipo() == TipoMovimento.SAIDA && rubrica.getTipo() != TipoRubrica.DESPESA) {
            throw new RegraNegocioException("Movimentação de SAÍDA deve ser associada a uma rubrica do tipo DESPESA.");
        }
        if (dto.getTipo() == TipoMovimento.ENTRADA && rubrica.getTipo() != TipoRubrica.RECEITA) {
            throw new RegraNegocioException(
                    "Movimentação de ENTRADA deve ser associada a uma rubrica do tipo RECEITA.");
        }

        // Atualiza o saldo da conta financeira
        if (dto.getTipo() == TipoMovimento.ENTRADA) {
            contaFinanceira
                    .setSaldoAtual(contaFinanceira.getSaldoAtual().add(new BigDecimal(dto.getValor().toString())));
        } else if (dto.getTipo() == TipoMovimento.SAIDA) {
            contaFinanceira
                    .setSaldoAtual(contaFinanceira.getSaldoAtual().subtract(new BigDecimal(dto.getValor().toString())));
        }
        contaFinanceiraRepository.save(contaFinanceira);

        // Cria o registro de movimento
        Movimento movimento = new Movimento();
        movimento.setTipo(dto.getTipo());
        movimento.setValor(dto.getValor());
        movimento.setContaFinanceira(contaFinanceira);
        movimento.setRubrica(rubrica);
        movimento.setCentroCusto(rubrica.getCentroCusto()); // Centro de custo da rubrica
        movimento.setDataHora(dto.getData().atStartOfDay()); // Usa a data do DTO
        movimento.setOrigemDestino(dto.getOrigemDestino());

        Movimento savedMovimento = movimentoRepository.save(movimento);

        // Atualiza entradas/saídas do Centro de Custo
        CentroCusto centroCusto = savedMovimento.getCentroCusto();
        if (savedMovimento.getTipo() == TipoMovimento.ENTRADA) {
            centroCusto.setEntradas(centroCusto.getEntradas().add(savedMovimento.getValor()));
        } else if (savedMovimento.getTipo() == TipoMovimento.SAIDA) {
            centroCusto.setSaidas(centroCusto.getSaidas().add(savedMovimento.getValor()));
        }
        centroCustoRepository.save(centroCusto);

        return savedMovimento;
    }

    public List<Movimento> filtrarMovimentos(ExtratoFiltroDto filtro) {
        List<Movimento> movimentos = movimentoRepository.findAll(); // Get all for in-memory filtering

        return movimentos.stream()
                .filter(movimento -> {
                    boolean match = true;

                    if (filtro.getDataInicio() != null) {
                        match = match && !movimento.getDataHora().toLocalDate().isBefore(filtro.getDataInicio());
                    }
                    if (filtro.getDataFim() != null) {
                        match = match && !movimento.getDataHora().toLocalDate().isAfter(filtro.getDataFim());
                    }
                    if (filtro.getContaFinanceiraId() != null) {
                        match = match && movimento.getContaFinanceira().getId().equals(filtro.getContaFinanceiraId());
                    }
                    if (filtro.getTipoMovimento() != null) {
                        match = match && movimento.getTipo() == filtro.getTipoMovimento();
                    }
                    if (filtro.getRubricaId() != null) {
                        match = match && movimento.getRubrica().getId().equals(filtro.getRubricaId());
                    }
                    return match;
                })
                .collect(Collectors.toList());
    }

    public DemonstrativoFinanceiroMensalDto gerarDemonstrativoFinanceiroMensal(int mes, int ano) {
        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Movimento> movimentosDoMes = movimentoRepository.findByDataHoraBetween(startOfMonth, endOfMonth);

        // Entradas (Crédito)
        List<RubricaGroupDto> entradasPorRubrica = movimentosDoMes.stream()
                .filter(movimento -> movimento.getTipo() == TipoMovimento.ENTRADA)
                .collect(Collectors.groupingBy(
                        movimento -> movimento.getRubrica().getNome(),
                        Collectors.reducing(BigDecimal.ZERO,
                                movimento -> movimento.getValor(),
                                BigDecimal::add)))
                .entrySet().stream()
                .map(entry -> new RubricaGroupDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(RubricaGroupDto::getNomeRubrica))
                .collect(Collectors.toList());

        BigDecimal totalReceitas = entradasPorRubrica.stream()
                .map(RubricaGroupDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Saídas (Débito)
        List<RubricaGroupDto> saidasPorRubrica = movimentosDoMes.stream()
                .filter(movimento -> movimento.getTipo() == TipoMovimento.SAIDA)
                .collect(Collectors.groupingBy(
                        movimento -> movimento.getRubrica().getNome(),
                        Collectors.reducing(BigDecimal.ZERO,
                                movimento -> movimento.getValor(),
                                BigDecimal::add)))
                .entrySet().stream()
                .map(entry -> new RubricaGroupDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(RubricaGroupDto::getNomeRubrica))
                .collect(Collectors.toList());

        BigDecimal totalDespesas = saidasPorRubrica.stream()
                .map(RubricaGroupDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Saldo do mês anterior (PLACEHOLDER)
        // Para uma implementação real, você precisaria calcular o saldo de todas as
        // contas financeiras
        // até o final do mês anterior. Isso envolveria somar todos os movimentos
        // anteriores ao startOfMonth
        // e considerar saldos iniciais das contas.
        BigDecimal saldoMesAnterior = BigDecimal.ZERO; // Placeholder

        // Resultado Operacional
        BigDecimal resultadoOperacional = saldoMesAnterior.add(totalReceitas).subtract(totalDespesas);

        return new DemonstrativoFinanceiroMensalDto(
                entradasPorRubrica,
                saidasPorRubrica,
                saldoMesAnterior,
                totalReceitas,
                totalDespesas,
                resultadoOperacional,
                mes,
                ano);
    }

}
