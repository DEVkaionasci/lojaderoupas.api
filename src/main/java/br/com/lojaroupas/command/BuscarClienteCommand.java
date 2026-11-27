package br.com.lojaroupas.command;

import br.com.lojaroupas.service.ClienteService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/clientes/{id} - detalhe de um cliente. */
public class BuscarClienteCommand implements Command {

    private final ClienteService service;

    public BuscarClienteCommand(ClienteService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.buscar(HttpUtil.idDaRota(request)));
    }
}
