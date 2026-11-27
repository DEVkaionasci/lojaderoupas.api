package br.com.lojaroupas.decorator;

import java.math.BigDecimal;

/** Componente concreto: o valor bruto dos itens, sem nenhum desconto. */
public class SubtotalPedido implements PrecoPedido {

    private final BigDecimal subtotal;

    public SubtotalPedido(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public BigDecimal getValor() {
        return subtotal;
    }

    @Override
    public String getDescricao() {
        return "";
    }
}
