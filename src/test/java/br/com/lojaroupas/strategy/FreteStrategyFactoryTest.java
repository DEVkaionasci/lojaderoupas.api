package br.com.lojaroupas.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreteStrategyFactoryTest {

    private static final BigDecimal VALOR_BAIXO = new BigDecimal("100.00");

    @Test
    void mesmoEstadoDaLojaUsaFreteLocal() {
        FreteStrategy estrategia = FreteStrategyFactory.para("BA", VALOR_BAIXO);
        assertEquals("LOCAL", estrategia.getNome());
        assertEquals("12.00", estrategia.calcular(VALOR_BAIXO).toPlainString());
    }

    @Test
    void outroEstadoDoNordesteUsaFreteRegional() {
        FreteStrategy estrategia = FreteStrategyFactory.para("pe", VALOR_BAIXO); // minusculo tambem funciona
        assertEquals("REGIONAL", estrategia.getNome());
        assertEquals("22.00", estrategia.calcular(VALOR_BAIXO).toPlainString());
    }

    @Test
    void demaisEstadosUsamFreteNacional() {
        FreteStrategy estrategia = FreteStrategyFactory.para("SP", VALOR_BAIXO);
        assertEquals("NACIONAL", estrategia.getNome());
        assertEquals("38.00", estrategia.calcular(VALOR_BAIXO).toPlainString());
    }

    @Test
    void ufDesconhecidaCaiNoFreteNacional() {
        assertEquals("NACIONAL", FreteStrategyFactory.para(null, VALOR_BAIXO).getNome());
        assertEquals("NACIONAL", FreteStrategyFactory.para("  ", VALOR_BAIXO).getNome());
    }

    @Test
    void valorAltoTemFreteGratisEmQualquerEstado() {
        BigDecimal valorAlto = new BigDecimal("300.00");
        FreteStrategy estrategia = FreteStrategyFactory.para("SP", valorAlto);
        assertEquals("GRATIS", estrategia.getNome());
        assertEquals("0.00", estrategia.calcular(valorAlto).toPlainString());
    }

    @Test
    void logoAbaixoDoLimiteAindaCobraFrete() {
        FreteStrategy estrategia = FreteStrategyFactory.para("SP", new BigDecimal("299.99"));
        assertEquals("NACIONAL", estrategia.getNome());
    }
}
