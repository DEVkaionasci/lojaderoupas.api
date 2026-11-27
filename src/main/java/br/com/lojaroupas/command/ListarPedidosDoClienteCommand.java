package br.com.lojaroupas.command;

import br.com.lojaroupas.service.PedidoService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/clientes/{id}/pedidos - pedidos de um cliente. */
public class ListarPedidosDoClienteCommand implements Command {

    private final PedidoService service;

    public ListarPedidosDoClienteCommand(PedidoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.listarPorCliente(HttpUtil.idDaRota(request)));
    }
}
