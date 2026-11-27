package br.com.lojaroupas.command;

import br.com.lojaroupas.service.ProdutoService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/produtos/{id} - detalhe de um produto. */
public class BuscarProdutoCommand implements Command {

    private final ProdutoService service;

    public BuscarProdutoCommand(ProdutoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.buscar(HttpUtil.idDaRota(request)));
    }
}
