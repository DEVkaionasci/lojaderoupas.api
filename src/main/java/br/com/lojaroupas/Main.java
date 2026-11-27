package br.com.lojaroupas;

import br.com.lojaroupas.controller.AutenticacaoInterceptor;
import br.com.lojaroupas.controller.FrontControllerServlet;
import br.com.lojaroupas.controller.Rotas;
import br.com.lojaroupas.controller.Router;
import br.com.lojaroupas.dao.ClienteDAO;
import br.com.lojaroupas.dao.ClienteDAOImpl;
import br.com.lojaroupas.dao.PedidoDAO;
import br.com.lojaroupas.dao.PedidoDAOImpl;
import br.com.lojaroupas.dao.ProdutoDAO;
import br.com.lojaroupas.dao.ProdutoDAOImpl;
import br.com.lojaroupas.infra.Config;
import br.com.lojaroupas.infra.ConnectionSingleton;
import br.com.lojaroupas.integration.CepGateway;
import br.com.lojaroupas.integration.ViaCepClient;
import br.com.lojaroupas.service.CepService;
import br.com.lojaroupas.service.ClienteService;
import br.com.lojaroupas.service.PedidoService;
import br.com.lojaroupas.service.ProdutoService;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ponto de partida: monta as pecas (DAO -> Service -> Command) e sobe o Tomcat embarcado.
 * Sem Spring: a "injecao de dependencia" aqui e feita manualmente, passando cada
 * objeto no construtor de quem precisa dele.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        verificarBanco();

        // 1) camada de persistencia (DAO)
        ProdutoDAO produtoDAO = new ProdutoDAOImpl();
        ClienteDAO clienteDAO = new ClienteDAOImpl();
        PedidoDAO pedidoDAO = new PedidoDAOImpl();

        // 2) integracao com a API externa
        CepGateway cepGateway = new ViaCepClient();

        // 3) camada de servico (regras de negocio)
        ProdutoService produtoService = new ProdutoService(produtoDAO);
        ClienteService clienteService = new ClienteService(clienteDAO, cepGateway);
        PedidoService pedidoService = new PedidoService(pedidoDAO, produtoDAO, clienteDAO);
        CepService cepService = new CepService(cepGateway);

        // 4) rotas -> commands, e o Front Controller que as usa
        Router router = Rotas.montar(produtoService, clienteService, pedidoService, cepService);
        FrontControllerServlet frontController =
                new FrontControllerServlet(router, new AutenticacaoInterceptor(Config.apiKey()));

        // 5) Tomcat embarcado: sem webapp/ e sem web.xml, o Servlet e registrado por codigo
        int porta = Config.porta();
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(porta);
        tomcat.getConnector(); // garante a criacao do conector HTTP

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "frontController", frontController);
        ctx.addServletMappingDecoded("/api/*", "frontController");

        tomcat.start();
        System.out.println("Loja de Roupas no ar: http://localhost:" + porta + "/api/produtos");
        tomcat.getServer().await();
    }

    /** Falha rapido, com mensagem clara, se o PostgreSQL nao estiver acessivel. */
    private static void verificarBanco() {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            System.out.println("Conexão com o PostgreSQL OK: " + Config.dbUrl());
        } catch (SQLException e) {
            System.err.println("Não foi possível conectar ao PostgreSQL (" + Config.dbUrl() + "): " + e.getMessage());
            System.err.println("Confira se o banco está rodando, se o schema.sql foi executado e as variáveis "
                    + "DB_URL, DB_USER e DB_PASSWORD.");
            System.exit(1);
        }
    }
}
