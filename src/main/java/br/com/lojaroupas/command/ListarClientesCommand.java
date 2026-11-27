package br.com.lojaroupas.command;

import br.com.lojaroupas.service.ClienteService;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/clientes - lista clientes (rota administrativa). */
public class ListarClientesCommand implements Command {

    private final ClienteService service;

    public ListarClientesCommand(ClienteService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.listar());
    }
}
