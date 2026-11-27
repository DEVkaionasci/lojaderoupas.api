package br.com.lojaroupas.decorator;

import java.math.BigDecimal;

/** 5% de desconto para pedidos com 3 ou mais pecas. */
public class DescontoQuantidadeDecorator extends DescontoDecorator {

    public DescontoQuantidadeDecorator(PrecoPedido decorado) {
        super(decorado);
    }

    @Override
    protected BigDecimal getPercentual() {
        return new BigDecimal("0.05");
    }

    @Override
    protected String getRotulo() {
        return "3+ peças 5%";
    }
}
