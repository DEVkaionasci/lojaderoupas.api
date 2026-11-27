package br.com.lojaroupas.command;

import br.com.lojaroupas.dto.ClienteRequestDTO;
import br.com.lojaroupas.service.ClienteService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** PUT /api/clientes/{id} - atualiza cliente. */
public class AtualizarClienteCommand implements Command {

    private final ClienteService service;

    public AtualizarClienteCommand(ClienteService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClienteRequestDTO dto = JsonUtil.lerCorpo(request, ClienteRequestDTO.class);
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.atualizar(HttpUtil.idDaRota(request), dto));
    }
}
