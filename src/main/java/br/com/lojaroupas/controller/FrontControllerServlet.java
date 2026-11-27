package br.com.lojaroupas.controller;

import br.com.lojaroupas.exception.AppException;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PADRAO: FRONT CONTROLLER.
 *
 * E o UNICO ponto de entrada da API (mapeado em /api/*). Todo o
 * pre-processamento transversal acontece aqui, antes de qualquer Command:
 *   1. configura codificacao UTF-8 e tipo JSON;
 *   2. resolve a rota (404 / 405);
 *   3. AUTENTICACAO das rotas administrativas (401);
 *   4. despacha para o Command;
 *   5. TRATAMENTO CENTRALIZADO DE EXCECOES: qualquer erro vira JSON com o status certo;
 *   6. registra um log por requisicao.
 */
public class FrontControllerServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(FrontControllerServlet.class.getName());

    private final Router router;
    private final AutenticacaoInterceptor autenticacao;

    public FrontControllerServlet(Router router, AutenticacaoInterceptor autenticacao) {
        this.router = router;
        this.autenticacao = autenticacao;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long inicio = System.currentTimeMillis();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try {
            Router.Resolucao resolucao = router.resolver(request.getPathInfo(), request.getMethod());
            if (resolucao.getRota().isProtegida()) {
                autenticacao.validar(request);
            }
            request.setAttribute(HttpUtil.ATRIBUTO_PARAMETRO, resolucao.getParametro());
            resolucao.getRota().getCommand().executar(request, response);
        } catch (AppException e) {
            // erros "esperados": validacao, nao encontrado, conflito, nao autorizado...
            JsonUtil.escreverErro(response, e.getStatus(), e.getMessage());
        } catch (Exception e) {
            // erro inesperado: registra o detalhe no log, mas nao vaza detalhes para o cliente
            LOG.log(Level.SEVERE, "Erro inesperado em " + request.getMethod() + " " + request.getPathInfo(), e);
            JsonUtil.escreverErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        } finally {
            LOG.info(request.getMethod() + " " + request.getPathInfo() + " -> " + response.getStatus()
                    + " (" + (System.currentTimeMillis() - inicio) + " ms)");
        }
    }
}
