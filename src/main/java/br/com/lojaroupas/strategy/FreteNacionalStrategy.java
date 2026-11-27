package br.com.lojaroupas.strategy;

import java.math.BigDecimal;

/** Entrega para o restante do Brasil. */
public class FreteNacionalStrategy implements FreteStrategy {

    @Override
    public String getNome() {
        return "NACIONAL";
    }

    @Override
    public BigDecimal calcular(BigDecimal valorPedido) {
        return new BigDecimal("38.00");
    }
}
