package br.com.sigest.tesouraria.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.repository.CobrancaRepository;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.dto.DashboardFinanceiroDto;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardFinanceiroServiceTest {

    @Mock
    private MovimentoRepository movimentoRepository;

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @InjectMocks
    private DashboardFinanceiroService dashboardFinanceiroService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalcularSaidasPorMes() {
        // Simular dados de saída
        when(movimentoRepository.sumByTipoAndPeriodo(any(TipoMovimento.class), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(new BigDecimal("1000.00"));

        // Chamar o método de teste
        LocalDateTime inicio = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        
        // Usar reflexão para acessar o método privado
        try {
            java.lang.reflect.Method method = DashboardFinanceiroService.class.getDeclaredMethod(
                "calcularSaidasPorMes", LocalDateTime.class, LocalDateTime.class);
            method.setAccessible(true);
            
            List<?> result = (List<?>) method.invoke(dashboardFinanceiroService, inicio, fim);
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
            System.out.println("Resultado do teste: " + result.size() + " meses de dados de saída");
        } catch (Exception e) {
            fail("Erro ao testar o método calcularSaidasPorMes: " + e.getMessage());
        }
    }

    @Test
    public void testGetDadosMensaisPorAno() {
        // Simular dados de entrada e saída
        when(movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.ENTRADA, 
            LocalDateTime.of(2023, 1, 1, 0, 0, 0), 
            LocalDateTime.of(2023, 1, 31, 23, 59, 59)))
            .thenReturn(new BigDecimal("5000.00"));
            
        when(movimentoRepository.sumByTipoAndPeriodo(TipoMovimento.SAIDA, 
            LocalDateTime.of(2023, 1, 1, 0, 0, 0), 
            LocalDateTime.of(2023, 1, 31, 23, 59, 59)))
            .thenReturn(new BigDecimal("3000.00"));

        // Chamar o método de teste
        DashboardFinanceiroDto result = dashboardFinanceiroService.getDadosMensaisPorAno(2023);
        
        assertNotNull(result);
        assertNotNull(result.getEntradasMensais());
        assertNotNull(result.getSaidasMensais());
        
        System.out.println("Entradas mensais: " + result.getEntradasMensais().size());
        System.out.println("Saídas mensais: " + result.getSaidasMensais().size());
    }
}