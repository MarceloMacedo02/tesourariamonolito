package br.com.sigest.tesouraria.domain.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ReconciliacaoMensalTest {

    @Test
    public void testCalculoSaldoFinal() {
        ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
        reconciliacao.setSaldoInicial(new BigDecimal("10457.22"));
        reconciliacao.setTotalEntradas(new BigDecimal("15615.00"));
        reconciliacao.setTotalSaidas(new BigDecimal("10330.53"));
        
        BigDecimal saldoFinalEsperado = new BigDecimal("15741.69");
        assertEquals(0, reconciliacao.getSaldoFinal().compareTo(saldoFinalEsperado));
    }
    
    @Test
    public void testCalculoComValoresNulos() {
        ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
        // Deixando os valores nulos para testar o tratamento
        
        BigDecimal saldoFinalEsperado = BigDecimal.ZERO;
        assertEquals(0, reconciliacao.getSaldoFinal().compareTo(saldoFinalEsperado));
    }
}