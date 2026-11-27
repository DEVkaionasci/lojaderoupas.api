package br.com.lojaroupas.command;

import br.com.lojaroupas.service.ProdutoService;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/produtos[?categoria=] - lista o catalogo. */
public class ListarProdutosCommand implements Command {

    private final ProdutoService service;

    public ListarProdutosCommand(ProdutoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String categoria = request.getParameter("categoria"); // filtro opcional: ?categoria=CAMISETA
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.listar(categoria));
    }
}
