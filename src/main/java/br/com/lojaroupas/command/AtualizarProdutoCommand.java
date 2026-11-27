package br.com.lojaroupas.command;

import br.com.lojaroupas.dto.ProdutoRequestDTO;
import br.com.lojaroupas.service.ProdutoService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** PUT /api/produtos/{id} - atualiza um produto (rota administrativa). */
public class AtualizarProdutoCommand implements Command {

    private final ProdutoService service;

    public AtualizarProdutoCommand(ProdutoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ProdutoRequestDTO dto = JsonUtil.lerCorpo(request, ProdutoRequestDTO.class);
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.atualizar(HttpUtil.idDaRota(request), dto));
    }
}
