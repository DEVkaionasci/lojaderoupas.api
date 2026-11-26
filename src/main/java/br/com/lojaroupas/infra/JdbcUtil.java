package br.com.lojaroupas.infra;

import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.exception.PersistenciaException;

import java.sql.Connection;
import java.sql.SQLException;

/** Pequenos auxiliares para os DAOs nao repetirem codigo JDBC. */
public final class JdbcUtil {

    private static final String UNIQUE_VIOLATION = "23505";
    private static final String FOREIGN_KEY_VIOLATION = "23503";

    private JdbcUtil() {
    }

    /** Converte um SQLException na excecao de aplicacao mais adequada. */
    public static RuntimeException traduzir(SQLException e, String mensagem) {
        if (FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
            return new ConflitoException("O registro está vinculado a outros dados e não pode ser alterado/removido");
        }
        if (UNIQUE_VIOLATION.equals(e.getSQLState())) {
            return new ConflitoException("Já existe um registro com esse valor único");
        }
        return new PersistenciaException(mensagem, e);
    }

    public static void rollbackSilencioso(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignorada) {
                // nada a fazer: a conexao sera fechada logo em seguida
            }
        }
    }

    public static void fecharSilencioso(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignorada) {
                // nada a fazer
            }
        }
    }
}
