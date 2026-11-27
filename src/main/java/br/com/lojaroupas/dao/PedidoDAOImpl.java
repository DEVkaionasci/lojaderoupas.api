package br.com.lojaroupas.dao;

import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.infra.ConnectionSingleton;
import br.com.lojaroupas.infra.JdbcUtil;
import br.com.lojaroupas.model.FormaPagamento;
import br.com.lojaroupas.model.ItemPedido;
import br.com.lojaroupas.model.Pedido;
import br.com.lojaroupas.model.StatusPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacao JDBC/PostgreSQL do PedidoDAO.
 * Operacoes que mexem em varias tabelas usam TRANSACAO: ou grava tudo, ou nada.
 */
public class PedidoDAOImpl implements PedidoDAO {

    private static final String COLUNAS = "id, cliente_id, data_criacao, status, forma_pagamento, cep_entrega, "
            + "uf_entrega, subtotal, desconto, frete, total, tipo_frete, descontos_aplicados";

    @Override
    public void inserir(Pedido pedido) {
        Connection conn = null;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            conn.setAutoCommit(false);

            inserirCabecalho(conn, pedido);
            for (ItemPedido item : pedido.getItens()) {
                baixarEstoque(conn, item);
                inserirItem(conn, pedido.getId(), item);
            }
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollbackSilencioso(conn);
            throw JdbcUtil.traduzir(e, "Erro ao registrar pedido");
        } catch (RuntimeException e) {
            JdbcUtil.rollbackSilencioso(conn);
            throw e;
        } finally {
            JdbcUtil.fecharSilencioso(conn);
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        String sql = "SELECT " + COLUNAS + " FROM pedido WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Pedido pedido = mapear(rs);
                pedido.setItens(carregarItens(conn, pedido.getId()));
                return Optional.of(pedido);
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao buscar pedido");
        }
    }

    @Override
    public List<Pedido> listar() {
        return consultar("SELECT " + COLUNAS + " FROM pedido ORDER BY id DESC", null);
    }

    @Override
    public List<Pedido> listarPorCliente(Long clienteId) {
        return consultar("SELECT " + COLUNAS + " FROM pedido WHERE cliente_id = ? ORDER BY id DESC", clienteId);
    }

    @Override
    public void atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        String sql = "UPDATE pedido SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus.name());
            stmt.setLong(2, pedidoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao atualizar status do pedido");
        }
    }

    @Override
    public void cancelar(Pedido pedido) {
        Connection conn = null;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE pedido SET status = ? WHERE id = ?")) {
                stmt.setString(1, StatusPedido.CANCELADO.name());
                stmt.setLong(2, pedido.getId());
                stmt.executeUpdate();
            }
            for (ItemPedido item : pedido.getItens()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE produto SET estoque = estoque + ? WHERE id = ?")) {
                    stmt.setInt(1, item.getQuantidade());
                    stmt.setLong(2, item.getProdutoId());
                    stmt.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollbackSilencioso(conn);
            throw JdbcUtil.traduzir(e, "Erro ao cancelar pedido");
        } finally {
            JdbcUtil.fecharSilencioso(conn);
        }
    }

    // ------------------------------------------------------------------ privados

    private void inserirCabecalho(Connection conn, Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (cliente_id, data_criacao, status, forma_pagamento, cep_entrega, "
                + "uf_entrega, subtotal, desconto, frete, total, tipo_frete, descontos_aplicados) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pedido.getClienteId());
            stmt.setTimestamp(2, Timestamp.valueOf(pedido.getDataCriacao()));
            stmt.setString(3, pedido.getStatus().name());
            stmt.setString(4, pedido.getFormaPagamento().name());
            stmt.setString(5, pedido.getCepEntrega());
            stmt.setString(6, pedido.getUfEntrega());
            stmt.setBigDecimal(7, pedido.getSubtotal());
            stmt.setBigDecimal(8, pedido.getDesconto());
            stmt.setBigDecimal(9, pedido.getFrete());
            stmt.setBigDecimal(10, pedido.getTotal());
            stmt.setString(11, pedido.getTipoFrete());
            stmt.setString(12, pedido.getDescontosAplicados());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setId(rs.getLong(1));
                }
            }
        }
    }

    /** O "AND estoque >= ?" garante que duas compras simultaneas nao deixem o estoque negativo. */
    private void baixarEstoque(Connection conn, ItemPedido item) throws SQLException {
        String sql = "UPDATE produto SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getQuantidade());
            stmt.setLong(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            if (stmt.executeUpdate() == 0) {
                throw new ConflitoException("Estoque insuficiente para o produto '" + item.getNomeProduto() + "'");
            }
        }
    }

    private void inserirItem(Connection conn, Long pedidoId, ItemPedido item) throws SQLException {
        String sql = "INSERT INTO item_pedido (pedido_id, produto_id, nome_produto, quantidade, preco_unitario) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pedidoId);
            stmt.setLong(2, item.getProdutoId());
            stmt.setString(3, item.getNomeProduto());
            stmt.setInt(4, item.getQuantidade());
            stmt.setBigDecimal(5, item.getPrecoUnitario());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                }
            }
        }
    }

    private List<Pedido> consultar(String sql, Long filtro) {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (filtro != null) {
                stmt.setLong(1, filtro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapear(rs));
                }
            }
            for (Pedido pedido : pedidos) {
                pedido.setItens(carregarItens(conn, pedido.getId()));
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao listar pedidos");
        }
        return pedidos;
    }

    private List<ItemPedido> carregarItens(Connection conn, Long pedidoId) throws SQLException {
        String sql = "SELECT id, produto_id, nome_produto, quantidade, preco_unitario "
                + "FROM item_pedido WHERE pedido_id = ? ORDER BY id";
        List<ItemPedido> itens = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(new ItemPedido(
                            rs.getLong("id"),
                            rs.getLong("produto_id"),
                            rs.getString("nome_produto"),
                            rs.getInt("quantidade"),
                            rs.getBigDecimal("preco_unitario")));
                }
            }
        }
        return itens;
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setClienteId(rs.getLong("cliente_id"));
        pedido.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
        pedido.setFormaPagamento(FormaPagamento.valueOf(rs.getString("forma_pagamento")));
        pedido.setCepEntrega(rs.getString("cep_entrega"));
        pedido.setUfEntrega(rs.getString("uf_entrega"));
        pedido.setSubtotal(rs.getBigDecimal("subtotal"));
        pedido.setDesconto(rs.getBigDecimal("desconto"));
        pedido.setFrete(rs.getBigDecimal("frete"));
        pedido.setTotal(rs.getBigDecimal("total"));
        pedido.setTipoFrete(rs.getString("tipo_frete"));
        pedido.setDescontosAplicados(rs.getString("descontos_aplicados"));
        return pedido;
    }
}
