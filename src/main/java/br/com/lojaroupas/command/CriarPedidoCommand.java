package br.com.lojaroupas.command;

import br.com.lojaroupas.dto.PedidoRequestDTO;
import br.com.lojaroupas.service.PedidoService;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** POST /api/pedidos - cria um pedido (aplica descontos e frete, baixa estoque). */
public class CriarPedidoCommand implements Command {

    private final PedidoService service;

    public CriarPedidoCommand(PedidoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PedidoRequestDTO dto = JsonUtil.lerCorpo(request, PedidoRequestDTO.class);
        JsonUtil.escrever(response, HttpServletResponse.SC_CREATED, service.criar(dto));
    }
}
