package br.com.lojaroupas.strategy;

import java.math.BigDecimal;

/** Entrega para outros estados do Nordeste. */
public class FreteRegionalStrategy implements FreteStrategy {

    @Override
    public String getNome() {
        return "REGIONAL";
    }

    @Override
    public BigDecimal calcular(BigDecimal valorPedido) {
        return new BigDecimal("22.00");
    }
}
