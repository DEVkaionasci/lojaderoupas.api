package br.com.lojaroupas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusPedidoTest {

    @Test
    void fluxoNormalDoPedido() {
        assertTrue(StatusPedido.CRIADO.podeIrPara(StatusPedido.PAGO));
        assertTrue(StatusPedido.PAGO.podeIrPara(StatusPedido.ENVIADO));
        assertTrue(StatusPedido.ENVIADO.podeIrPara(StatusPedido.ENTREGUE));
    }

    @Test
    void podeCancelarAteSerEnviado() {
        assertTrue(StatusPedido.CRIADO.podeIrPara(StatusPedido.CANCELADO));
        assertTrue(StatusPedido.PAGO.podeIrPara(StatusPedido.CANCELADO));
        assertFalse(StatusPedido.ENVIADO.podeIrPara(StatusPedido.CANCELADO));
    }

    @Test
    void naoPulaEtapasNemVolta() {
        assertFalse(StatusPedido.CRIADO.podeIrPara(StatusPedido.ENVIADO));
        assertFalse(StatusPedido.PAGO.podeIrPara(StatusPedido.CRIADO));
    }

    @Test
    void estadosFinaisNaoMudam() {
        for (StatusPedido destino : StatusPedido.values()) {
            assertFalse(StatusPedido.ENTREGUE.podeIrPara(destino));
            assertFalse(StatusPedido.CANCELADO.podeIrPara(destino));
        }
    }
}
