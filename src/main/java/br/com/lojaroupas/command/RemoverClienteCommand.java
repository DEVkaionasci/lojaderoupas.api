package br.com.lojaroupas.command;

import br.com.lojaroupas.service.ClienteService;
import br.com.lojaroupas.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** DELETE /api/clientes/{id} - remove cliente (rota administrativa). */
public class RemoverClienteCommand implements Command {

    private final ClienteService service;

    public RemoverClienteCommand(ClienteService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.remover(HttpUtil.idDaRota(request));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
