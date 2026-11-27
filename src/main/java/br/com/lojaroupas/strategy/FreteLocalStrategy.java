package br.com.lojaroupas.strategy;

import java.math.BigDecimal;

/** Entrega dentro do mesmo estado da loja. */
public class FreteLocalStrategy implements FreteStrategy {

    @Override
    public String getNome() {
        return "LOCAL";
    }

    @Override
    public BigDecimal calcular(BigDecimal valorPedido) {
        return new BigDecimal("12.00");
    }
}
