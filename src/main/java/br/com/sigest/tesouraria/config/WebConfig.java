package br.com.sigest.tesouraria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(new Locale("pt", "BR"));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(BigDecimal.class, new MoneyFormatter());
    }

    public static class MoneyFormatter implements org.springframework.format.Formatter<BigDecimal> {

        private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        @Override
        public String print(BigDecimal object, Locale locale) {
            return object == null ? "" : numberFormat.format(object);
        }

        @Override
        public BigDecimal parse(String text, Locale locale) throws ParseException {
            if (text == null || text.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            try {
                // Remover o prefixo 'R$ ' se estiver presente
                if (text.startsWith("R$ ")) {
                    text = text.substring(3);
                }
                // Substituir vírgula por ponto para o parser
                text = text.replace(".", "").replace(",", ".");
                return new BigDecimal(text);
            } catch (NumberFormatException e) {
                throw new ParseException("Unable to parse '" + text + "' to BigDecimal", 0);
            }
        }
    }
}