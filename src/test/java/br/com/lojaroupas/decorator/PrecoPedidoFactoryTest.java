package br.com.lojaroupas.decorator;

import br.com.lojaroupas.model.FormaPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrecoPedidoFactoryTest {

    private static final BigDecimal SUBTOTAL = new BigDecimal("200.00");

    @Test
    void semDescontoMantemOSubtotal() {
        PrecoPedido preco = PrecoPedidoFactory.criar(SUBTOTAL, FormaPagamento.CARTAO, 2);
        assertEquals("200.00", preco.getValor().toPlainString());
        assertEquals("", preco.getDescricao());
    }

    @Test
    void pixDaDezPorCento() {
        PrecoPedido preco = PrecoPedidoFactory.criar(SUBTOTAL, FormaPagamento.PIX, 1);
        assertEquals("180.00", preco.getValor().toPlainString());
        assertEquals("PIX 10%", preco.getDescricao());
    }

    @Test
    void tresPecasDaoCincoPorCento() {
        PrecoPedido preco = PrecoPedidoFactory.criar(SUBTOTAL, FormaPagamento.BOLETO, 3);
        assertEquals("190.00", preco.getValor().toPlainString());
        assertEquals("3+ peças 5%", preco.getDescricao());
    }

    @Test
    void descontosSaoEmpilhadosUmSobreOOutro() {
        PrecoPedido preco = PrecoPedidoFactory.criar(SUBTOTAL, FormaPagamento.PIX, 5);
        // 200,00 -10% = 180,00 ; 180,00 -5% = 171,00
        assertEquals("171.00", preco.getValor().toPlainString());
        assertEquals("PIX 10% + 3+ peças 5%", preco.getDescricao());
    }

    @Test
    void decoratorsPodemSerMontadosManualmenteEmQualquerOrdem() {
        PrecoPedido preco = new DescontoPixDecorator(new DescontoQuantidadeDecorator(new SubtotalPedido(SUBTOTAL)));
        // 200,00 -5% = 190,00 ; 190,00 -10% = 171,00
        assertEquals("171.00", preco.getValor().toPlainString());
    }

    @Test
    void arredondaParaDuasCasasComHalfUp() {
        PrecoPedido preco = new DescontoPixDecorator(new SubtotalPedido(new BigDecimal("10.05")));
        // 10% de 10,05 = 1,005 -> 1,01 ; 10,05 - 1,01 = 9,04
        assertEquals("9.04", preco.getValor().toPlainString());
    }
}
