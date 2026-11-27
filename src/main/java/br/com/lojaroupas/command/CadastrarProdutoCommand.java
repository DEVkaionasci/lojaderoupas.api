package br.com.lojaroupas.command;

import br.com.lojaroupas.dto.ProdutoRequestDTO;
import br.com.lojaroupas.service.ProdutoService;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** POST /api/produtos - cadastra um produto (rota administrativa). */
public class CadastrarProdutoCommand implements Command {

    private final ProdutoService service;

    public CadastrarProdutoCommand(ProdutoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ProdutoRequestDTO dto = JsonUtil.lerCorpo(request, ProdutoRequestDTO.class);
        JsonUtil.escrever(response, HttpServletResponse.SC_CREATED, service.criar(dto));
    }
}
