package br.com.lojaroupas.model;

/**
 * Ciclo de vida de um pedido.
 * CRIADO -> PAGO -> ENVIADO -> ENTREGUE (e CANCELADO enquanto ainda nao foi enviado).
 */
public enum StatusPedido {
    CRIADO,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO;

    public boolean podeIrPara(StatusPedido destino) {
        switch (this) {
            case CRIADO:
                return destino == PAGO || destino == CANCELADO;
            case PAGO:
                return destino == ENVIADO || destino == CANCELADO;
            case ENVIADO:
                return destino == ENTREGUE;
            default:
                return false; // ENTREGUE e CANCELADO sao estados finais
        }
    }
}
