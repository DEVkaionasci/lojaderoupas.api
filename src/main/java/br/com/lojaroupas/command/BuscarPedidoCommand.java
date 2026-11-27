package br.com.lojaroupas.command;

import br.com.lojaroupas.service.PedidoService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/pedidos/{id} - detalhe de um pedido. */
public class BuscarPedidoCommand implements Command {

    private final PedidoService service;

    public BuscarPedidoCommand(PedidoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.buscar(HttpUtil.idDaRota(request)));
    }
}
