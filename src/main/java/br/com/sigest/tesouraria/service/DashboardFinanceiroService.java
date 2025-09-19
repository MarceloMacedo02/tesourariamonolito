package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.repository.CobrancaRepository;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.dto.DadoGraficoDto;
import br.com.sigest.tesouraria.dto.DashboardFinanceiroDto;

@Service
public class DashboardFinanceiroService {
    
    @Autowired
    private MovimentoRepository movimentoRepository;
    
    @Autowired
    private SocioRepository socioRepository;
    
    @Autowired
    private CobrancaRepository cobrancaRepository;
    
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    
    public DashboardFinanceiroDto getDadosDashboard() {
        DashboardFinanceiroDto dto = new DashboardFinanceiroDto();
        
        // Dados mensais (últimos 12 meses)
        LocalDateTime fimMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).plusMonths(1).minusSeconds(1);
        LocalDateTime inicioMes = fimMes.minusMonths(11).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        // Dados anuais (últimos 4 anos)
        LocalDateTime fimAno = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).plusYears(1).minusSeconds(1);
        LocalDateTime inicioAno = fimAno.minusYears(3).withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        
        // Calcular entradas, saídas e saldos mensais
        dto.setEntradasMensais(calcularEntradasPorMes(inicioMes, fimMes));
        dto.setSaidasMensais(calcularSaidasPorMes(inicioMes, fimMes));
        dto.setSaldosMensais(calcularSaldosPorMes(inicioMes, fimMes));
        
        // Calcular entradas, saídas e saldos anuais
        dto.setEntradasAnuais(calcularEntradasPorAno(inicioAno, fimAno));
        dto.setSaidasAnuais(calcularSaidasPorAno(inicioAno, fimAno));
        dto.setSaldosAnuais(calcularSaldosPorAno(inicioAno, fimAno));
        
        // Dados de sócios
        dto.setTotalSocios(socioRepository.count());
        dto.setSociosFrequentes(socioRepository.findByStatus(StatusSocio.FREQUENTE).size());
        // Assumindo que sócios não frequentes são todos os sócios que não são frequentes
        // Você pode precisar ajustar essa lógica com base nos requisitos específicos do negócio
        dto.setSociosNaoFrequentes(socioRepository.count() - socioRepository.findByStatus(StatusSocio.FREQUENTE).size());
        
        // Dados de inadimplência
        List<Socio> inadimplentes = socioRepository.findSociosInadimplentes();
        dto.setTotalInadimplentes(inadimplentes.size());
        
        // Total a receber
        BigDecimal totalAReceber = cobrancaRepository.sumTotalAReceber();
        dto.setTotalAReceber(totalAReceber != null ? totalAReceber : BigDecimal.ZERO);
        
        // Movimentações mensais
        dto.setMovimentacoesMensais(calcularMovimentacoesPorMes(inicioMes, fimMes));
        
        return dto;
    }
    
    public DashboardFinanceiroDto getDadosMensaisPorAno(int ano) {
        DashboardFinanceiroDto dto = new DashboardFinanceiroDto();
        
        // Criar período para o ano especificado
        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0, 0);
        LocalDateTime fimAno = inicioAno.plusYears(1).minusSeconds(1);
        
        // Calcular entradas e saídas mensais para o ano especificado
        dto.setEntradasMensais(calcularEntradasPorMes(inicioAno, fimAno));
        dto.setSaidasMensais(calcularSaidasPorMes(inicioAno, fimAno));
        
        return dto;
    }
    
    private List<DadoGraficoDto> calcularEntradasPorMes(LocalDateTime inicio, LocalDateTime fim) {
        List<DadoGraficoDto> dados = new ArrayList<>();
        LocalDateTime dataAtual = inicio;
        
        // Determinar o número de meses a iterar com base no período
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(
            inicio.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
            fim.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        ) + 1;
        
        for (int i = 0; i < meses; i++) {
            LocalDateTime inicioMes = dataAtual.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimMes = inicioMes.plusMonths(1).minusSeconds(1);
            
            // Obter entradas diretamente do repositório
            BigDecimal total = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.ENTRADA, inicioMes, fimMes);
                
            String nomeMes = inicioMes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            dados.add(new DadoGraficoDto(nomeMes, total != null ? total : BigDecimal.ZERO));
            dataAtual = dataAtual.plusMonths(1);
        }
        return dados;
    }
    
    private List<DadoGraficoDto> calcularSaidasPorMes(LocalDateTime inicio, LocalDateTime fim) {
        List<DadoGraficoDto> dados = new ArrayList<>();
        LocalDateTime dataAtual = inicio;
        
        // Determinar o número de meses a iterar com base no período
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(
            inicio.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
            fim.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        ) + 1;
        
        for (int i = 0; i < meses; i++) {
            LocalDateTime inicioMes = dataAtual.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimMes = inicioMes.plusMonths(1).minusSeconds(1);
            
            // Obter saídas diretamente do repositório
            BigDecimal total = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.SAIDA, inicioMes, fimMes);
                
            String nomeMes = inicioMes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            dados.add(new DadoGraficoDto(nomeMes, total != null ? total : BigDecimal.ZERO));
            dataAtual = dataAtual.plusMonths(1);
        }
        return dados;
    }
    
    private List<DadoGraficoDto> calcularSaldosPorMes(LocalDateTime inicio, LocalDateTime fim) {
        List<DadoGraficoDto> dados = new ArrayList<>();
        LocalDateTime dataAtual = inicio;
        
        // Determinar o número de meses a iterar com base no período
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(
            inicio.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
            fim.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        ) + 1;
        
        for (int i = 0; i < meses; i++) {
            LocalDateTime inicioMes = dataAtual.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimMes = inicioMes.plusMonths(1).minusSeconds(1);
            
            // Obter entradas e saídas diretamente do repositório
            BigDecimal entradas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.ENTRADA, inicioMes, fimMes);
            BigDecimal saidas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.SAIDA, inicioMes, fimMes);
            
            BigDecimal saldo = (entradas != null ? entradas : BigDecimal.ZERO).subtract(saidas != null ? saidas : BigDecimal.ZERO);
            String nomeMes = inicioMes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            dados.add(new DadoGraficoDto(nomeMes, saldo));
            dataAtual = dataAtual.plusMonths(1);
        }
        return dados;
    }
    
    private List<DadoGraficoDto> calcularEntradasPorAno(LocalDateTime inicio, LocalDateTime fim) {
        List<DadoGraficoDto> dados = new ArrayList<>();
        LocalDateTime dataAtual = inicio;
        
        // Determinar o número de anos a iterar com base no período
        long anos = java.time.temporal.ChronoUnit.YEARS.between(
            inicio.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0),
            fim.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0)
        ) + 1;
        
        for (int i = 0; i < anos; i++) {
            LocalDateTime inicioAno = dataAtual.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimAno = inicioAno.plusYears(1).minusSeconds(1);
            
            // Obter entradas diretamente do repositório
            BigDecimal total = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.ENTRADA, inicioAno, fimAno);
                
            dados.add(new DadoGraficoDto(String.valueOf(inicioAno.getYear()), total != null ? total : BigDecimal.ZERO));
            dataAtual = dataAtual.plusYears(1);
        }
        return dados;
    }
    
    private List<DadoGraficoDto> calcularSaidasPorAno(LocalDateTime inicio, LocalDateTime fim) {
        List<DadoGraficoDto> dados = new ArrayList<>();
        LocalDateTime dataAtual = inicio;
        
        // Determinar o número de anos a iterar com base no período
        long anos = java.time.temporal.ChronoUnit.YEARS.between(
            inicio.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0),
            fim.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0)
        ) + 1;
        
        for (int i = 0; i < anos; i++) {
            LocalDateTime inicioAno = dataAtual.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimAno = inicioAno.plusYears(1).minusSeconds(1);
            
            // Obter saídas diretamente do repositório
            BigDecimal total = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.SAIDA, inicioAno, fimAno);
                
            dados.add(new DadoGraficoDto(String.valueOf(inicioAno.getYear()), total != null ? total : BigDecimal.ZERO));
            dataAtual = dataAtual.plusYears(1);
        }
        return dados;
    }
    
    private List<DadoGraficoDto> calcularSaldosPorAno(LocalDateTime inicio, LocalDateTime fim) {
        List<DadoGraficoDto> dados = new ArrayList<>();
        LocalDateTime dataAtual = inicio;
        
        // Determinar o número de anos a iterar com base no período
        long anos = java.time.temporal.ChronoUnit.YEARS.between(
            inicio.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0),
            fim.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0)
        ) + 1;
        
        for (int i = 0; i < anos; i++) {
            LocalDateTime inicioAno = dataAtual.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimAno = inicioAno.plusYears(1).minusSeconds(1);
            
            // Obter entradas e saídas diretamente do repositório
            BigDecimal entradas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.ENTRADA, inicioAno, fimAno);
            BigDecimal saidas = movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.SAIDA, inicioAno, fimAno);
            
            BigDecimal saldo = (entradas != null ? entradas : BigDecimal.ZERO).subtract(saidas != null ? saidas : BigDecimal.ZERO);
            dados.add(new DadoGraficoDto(String.valueOf(inicioAno.getYear()), saldo));
            dataAtual = dataAtual.plusYears(1);
        }
        return dados;
    }
    
    private List<DadoGraficoDto> calcularMovimentacoesPorMes(LocalDateTime inicio, LocalDateTime fim) {
        // Obter todas as movimentações no período
        List<Movimento> movimentos = movimentoRepository.findByDataHoraBetween(inicio, fim);
        
        // Agrupar movimentações por mês
        return movimentos.stream()
            .collect(Collectors.groupingBy(
                m -> m.getDataHora().getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Movimento::getValor,
                    BigDecimal::add
                )
            ))
            .entrySet()
            .stream()
            .map(entry -> new DadoGraficoDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}