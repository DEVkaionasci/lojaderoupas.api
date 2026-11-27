package br.com.lojaroupas.dao;

import br.com.lojaroupas.model.Pedido;
import br.com.lojaroupas.model.StatusPedido;

import java.util.List;
import java.util.Optional;

/** Contrato de acesso a dados de Pedido (que inclui os itens do pedido). */
public interface PedidoDAO {

    /** Grava pedido + itens e baixa o estoque, tudo na mesma transacao. */
    void inserir(Pedido pedido);

    Optional<Pedido> buscarPorId(Long id);

    List<Pedido> listar();

    List<Pedido> listarPorCliente(Long clienteId);

    void atualizarStatus(Long pedidoId, StatusPedido novoStatus);

    /** Marca como CANCELADO e devolve os itens ao estoque, na mesma transacao. */
    void cancelar(Pedido pedido);
}
