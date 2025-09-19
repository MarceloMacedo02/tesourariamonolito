package br.com.sigest.tesouraria.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MoneyFormatterTest {

    private final WebConfig.MoneyFormatter moneyFormatter = new WebConfig.MoneyFormatter();

    @Test
    public void testPrint() {
        BigDecimal value = new BigDecimal("1234.56");
        String formatted = moneyFormatter.print(value, new Locale("pt", "BR"));
        // Remover espaços em branco extras que podem ser adicionados pelo formato de moeda
        formatted = formatted.replace("\u00A0", " ").trim();
        assertEquals("R$ 1.234,56", formatted);
    }

    @Test
    public void testPrintWithNull() {
        String formatted = moneyFormatter.print(null, new Locale("pt", "BR"));
        assertEquals("", formatted);
    }

    @Test
    public void testParse() throws ParseException {
        String text = "R$ 1.234,56";
        BigDecimal parsed = moneyFormatter.parse(text, new Locale("pt", "BR"));
        assertEquals(new BigDecimal("1234.56"), parsed);
    }

    @Test
    public void testParseWithEmptyString() throws ParseException {
        String text = "";
        BigDecimal parsed = moneyFormatter.parse(text, new Locale("pt", "BR"));
        assertEquals(BigDecimal.ZERO, parsed);
    }

    @Test
    public void testParseWithNull() throws ParseException {
        String text = null;
        BigDecimal parsed = moneyFormatter.parse(text, new Locale("pt", "BR"));
        assertEquals(BigDecimal.ZERO, parsed);
    }

    @Test
    public void testParseInvalidFormat() {
        String text = "invalid";
        assertThrows(ParseException.class, () -> {
            moneyFormatter.parse(text, new Locale("pt", "BR"));
        });
    }
}