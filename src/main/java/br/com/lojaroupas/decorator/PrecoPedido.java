package br.com.lojaroupas.decorator;

import java.math.BigDecimal;

/**
 * PADRAO GoF: DECORATOR (componente).
 * Representa o valor de um pedido. Pode ser o subtotal "puro" ou o subtotal
 * embrulhado por um ou mais descontos. Como todos implementam a mesma interface,
 * os descontos podem ser empilhados em qualquer combinacao.
 */
public interface PrecoPedido {

    BigDecimal getValor();

    /** Texto com os descontos aplicados ate aqui (vazio se nao houver). */
    String getDescricao();
}
