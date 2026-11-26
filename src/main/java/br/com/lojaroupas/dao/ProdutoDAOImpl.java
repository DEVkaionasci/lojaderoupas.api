package br.com.lojaroupas.dao;

import br.com.lojaroupas.infra.ConnectionSingleton;
import br.com.lojaroupas.infra.JdbcUtil;
import br.com.lojaroupas.model.Categoria;
import br.com.lojaroupas.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementacao JDBC/PostgreSQL do ProdutoDAO. */
public class ProdutoDAOImpl implements ProdutoDAO {

    private static final String COLUNAS = "id, nome, descricao, categoria, tamanho, cor, preco, estoque";

    @Override
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, descricao, categoria, tamanho, cor, preco, estoque) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setString(3, produto.getCategoria().name());
            stmt.setString(4, produto.getTamanho());
            stmt.setString(5, produto.getCor());
            stmt.setBigDecimal(6, produto.getPreco());
            stmt.setInt(7, produto.getEstoque());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao cadastrar produto");
        }
    }

    @Override
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, categoria = ?, tamanho = ?, cor = ?, "
                + "preco = ?, estoque = ? WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setString(3, produto.getCategoria().name());
            stmt.setString(4, produto.getTamanho());
            stmt.setString(5, produto.getCor());
            stmt.setBigDecimal(6, produto.getPreco());
            stmt.setInt(7, produto.getEstoque());
            stmt.setLong(8, produto.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao atualizar produto");
        }
    }

    @Override
    public boolean remover(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao remover produto");
        }
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        String sql = "SELECT " + COLUNAS + " FROM produto WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao buscar produto");
        }
    }

    @Override
    public List<Produto> listar() {
        return consultar("SELECT " + COLUNAS + " FROM produto ORDER BY id", null);
    }

    @Override
    public List<Produto> listarPorCategoria(Categoria categoria) {
        return consultar("SELECT " + COLUNAS + " FROM produto WHERE categoria = ? ORDER BY id", categoria.name());
    }

    private List<Produto> consultar(String sql, String filtro) {
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (filtro != null) {
                stmt.setString(1, filtro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao listar produtos");
        }
        return produtos;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                Categoria.valueOf(rs.getString("categoria")),
                rs.getString("tamanho"),
                rs.getString("cor"),
                rs.getBigDecimal("preco"),
                rs.getInt("estoque"));
    }
}
