package br.com.lojaroupas.dao;

import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.infra.ConnectionSingleton;
import br.com.lojaroupas.infra.JdbcUtil;
import br.com.lojaroupas.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementacao JDBC/PostgreSQL do ClienteDAO. */
public class ClienteDAOImpl implements ClienteDAO {

    private static final String UNIQUE_VIOLATION = "23505";
    private static final String COLUNAS = "id, nome, email, senha_hash, cep, logradouro, bairro, cidade, uf";

    @Override
    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, email, senha_hash, cep, logradouro, bairro, cidade, uf) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getSenhaHash());
            stmt.setString(4, cliente.getCep());
            stmt.setString(5, cliente.getLogradouro());
            stmt.setString(6, cliente.getBairro());
            stmt.setString(7, cliente.getCidade());
            stmt.setString(8, cliente.getUf());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw traduzirCliente(e, "Erro ao cadastrar cliente");
        }
    }

    @Override
    public boolean atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, email = ?, senha_hash = ?, cep = ?, logradouro = ?, "
                + "bairro = ?, cidade = ?, uf = ? WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getSenhaHash());
            stmt.setString(4, cliente.getCep());
            stmt.setString(5, cliente.getLogradouro());
            stmt.setString(6, cliente.getBairro());
            stmt.setString(7, cliente.getCidade());
            stmt.setString(8, cliente.getUf());
            stmt.setLong(9, cliente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traduzirCliente(e, "Erro ao atualizar cliente");
        }
    }

    @Override
    public boolean remover(Long id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao remover cliente");
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        String sql = "SELECT " + COLUNAS + " FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao buscar cliente");
        }
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT " + COLUNAS + " FROM cliente ORDER BY id";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw JdbcUtil.traduzir(e, "Erro ao listar clientes");
        }
        return clientes;
    }

    /** Na tabela cliente o unico campo UNIQUE e o e-mail. */
    private RuntimeException traduzirCliente(SQLException e, String mensagem) {
        if (UNIQUE_VIOLATION.equals(e.getSQLState())) {
            return new ConflitoException("Já existe um cliente cadastrado com esse e-mail");
        }
        return JdbcUtil.traduzir(e, mensagem);
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha_hash"),
                rs.getString("cep"),
                rs.getString("logradouro"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("uf"));
    }
}
