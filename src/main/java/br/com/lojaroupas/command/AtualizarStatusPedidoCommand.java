package br.com.lojaroupas.command;

import br.com.lojaroupas.dto.StatusRequestDTO;
import br.com.lojaroupas.service.PedidoService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** PUT /api/pedidos/{id}/status - muda o status do pedido (rota administrativa). */
public class AtualizarStatusPedidoCommand implements Command {

    private final PedidoService service;

    public AtualizarStatusPedidoCommand(PedidoService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatusRequestDTO dto = JsonUtil.lerCorpo(request, StatusRequestDTO.class);
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.atualizarStatus(HttpUtil.idDaRota(request), dto));
    }
}
