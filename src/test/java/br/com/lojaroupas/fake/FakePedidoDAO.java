package br.com.lojaroupas.fake;

import br.com.lojaroupas.dao.PedidoDAO;
import br.com.lojaroupas.model.ItemPedido;
import br.com.lojaroupas.model.Pedido;
import br.com.lojaroupas.model.StatusPedido;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO em memoria; tambem imita o que o DAO real faz com o estoque (baixa ao inserir, devolve ao cancelar). */
public class FakePedidoDAO implements PedidoDAO {

    private final Map<Long, Pedido> dados = new LinkedHashMap<>();
    private final FakeProdutoDAO produtos;
    private long sequencia = 1;

    public FakePedidoDAO(FakeProdutoDAO produtos) {
        this.produtos = produtos;
    }

    @Override
    public void inserir(Pedido pedido) {
        pedido.setId(sequencia++);
        for (ItemPedido item : pedido.getItens()) {
            produtos.buscarPorId(item.getProdutoId()).ifPresent(p -> p.setEstoque(p.getEstoque() - item.getQuantidade()));
        }
        dados.put(pedido.getId(), pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Pedido> listar() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public List<Pedido> listarPorCliente(Long clienteId) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : dados.values()) {
            if (p.getClienteId().equals(clienteId)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public void atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        dados.get(pedidoId).setStatus(novoStatus);
    }

    @Override
    public void cancelar(Pedido pedido) {
        dados.get(pedido.getId()).setStatus(StatusPedido.CANCELADO);
        for (ItemPedido item : pedido.getItens()) {
            produtos.buscarPorId(item.getProdutoId()).ifPresent(p -> p.setEstoque(p.getEstoque() + item.getQuantidade()));
        }
    }
}
