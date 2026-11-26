package br.com.lojaroupas.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PADRAO GoF: SINGLETON.
 *
 * Existe uma unica instancia que guarda a configuracao de acesso ao banco.
 * Diferente do exemplo da aula (que guarda UMA Connection compartilhada), aqui
 * cada chamada a getConnection() devolve uma conexao nova. Motivo: o Tomcat
 * atende varias requisicoes ao mesmo tempo (threads) e uma conexao unica
 * compartilhada nao e segura para transacoes (ex.: criar pedido + baixar
 * estoque). O DAO fecha a conexao com try-with-resources.
 * Em producao, o ideal seria um connection pool (ex.: HikariCP).
 */
public class ConnectionSingleton {

    private static ConnectionSingleton instance;

    private final String url;
    private final String user;
    private final String password;

    private ConnectionSingleton() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado", e);
        }
        this.url = Config.dbUrl();
        this.user = Config.dbUser();
        this.password = Config.dbPassword();
    }

    /** synchronized evita que duas threads criem duas instancias ao mesmo tempo. */
    public static synchronized ConnectionSingleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionSingleton();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
