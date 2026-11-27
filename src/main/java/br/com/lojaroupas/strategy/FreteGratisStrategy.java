package br.com.lojaroupas.strategy;

import java.math.BigDecimal;

/** Frete gratis para pedidos de valor alto. */
public class FreteGratisStrategy implements FreteStrategy {

    @Override
    public String getNome() {
        return "GRATIS";
    }

    @Override
    public BigDecimal calcular(BigDecimal valorPedido) {
        return new BigDecimal("0.00");
    }
}
