package br.com.lojaroupas.strategy;

import java.math.BigDecimal;

/**
 * PADRAO GoF: STRATEGY.
 * Cada forma de calcular o frete e uma estrategia intercambiavel. O PedidoService
 * so conhece esta interface; qual regra usar e decidido pela FreteStrategyFactory.
 * Para criar uma nova regra (ex.: frete expresso) basta criar outra classe,
 * sem alterar o PedidoService.
 */
public interface FreteStrategy {

    /** Nome gravado no pedido (ex.: LOCAL, REGIONAL, NACIONAL, GRATIS). */
    String getNome();

    BigDecimal calcular(BigDecimal valorPedido);
}
