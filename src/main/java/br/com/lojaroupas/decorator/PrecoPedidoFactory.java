package br.com.lojaroupas.decorator;

import br.com.lojaroupas.model.FormaPagamento;

import java.math.BigDecimal;

/** Monta a "pilha" de decorators conforme as regras do pedido. */
public final class PrecoPedidoFactory {

    public static final int QUANTIDADE_MINIMA_DESCONTO = 3;

    private PrecoPedidoFactory() {
    }

    public static PrecoPedido criar(BigDecimal subtotal, FormaPagamento forma, int quantidadeTotal) {
        PrecoPedido preco = new SubtotalPedido(subtotal);
        if (forma == FormaPagamento.PIX) {
            preco = new DescontoPixDecorator(preco);
        }
        if (quantidadeTotal >= QUANTIDADE_MINIMA_DESCONTO) {
            preco = new DescontoQuantidadeDecorator(preco);
        }
        return preco;
    }
}
