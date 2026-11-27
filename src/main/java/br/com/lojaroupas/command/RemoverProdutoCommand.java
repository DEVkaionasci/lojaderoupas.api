package br.com.lojaroupas.command;

import br.com.lojaroupas.service.ProdutoService;
import br.com.lojaroupas.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** DELETE /api/produtos/{id} - remove um produto (rota administrativa). */
public class RemoverProdutoCommand implements Command {

    private final ProdutoService service;

    public RemoverProdutoCommand(ProdutoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.remover(HttpUtil.idDaRota(request));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
