package br.com.lojaroupas.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decorator abstrato: guarda o PrecoPedido "embrulhado" e aplica um percentual
 * de desconto sobre o valor dele. As subclasses so dizem qual e o percentual.
 */
public abstract class DescontoDecorator implements PrecoPedido {

    protected final PrecoPedido decorado;

    protected DescontoDecorator(PrecoPedido decorado) {
        this.decorado = decorado;
    }

    /** Percentual em formato decimal (0.10 = 10%). */
    protected abstract BigDecimal getPercentual();

    protected abstract String getRotulo();

    @Override
    public BigDecimal getValor() {
        BigDecimal base = decorado.getValor();
        BigDecimal desconto = base.multiply(getPercentual()).setScale(2, RoundingMode.HALF_UP);
        return base.subtract(desconto);
    }

    @Override
    public String getDescricao() {
        String anterior = decorado.getDescricao();
        return anterior.isEmpty() ? getRotulo() : anterior + " + " + getRotulo();
    }
}
