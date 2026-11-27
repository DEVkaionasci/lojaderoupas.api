package br.com.lojaroupas.decorator;

import java.math.BigDecimal;

/** 10% de desconto para pagamento via PIX. */
public class DescontoPixDecorator extends DescontoDecorator {

    public DescontoPixDecorator(PrecoPedido decorado) {
        super(decorado);
    }

    @Override
    protected BigDecimal getPercentual() {
        return new BigDecimal("0.10");
    }

    @Override
    protected String getRotulo() {
        return "PIX 10%";
    }
}
