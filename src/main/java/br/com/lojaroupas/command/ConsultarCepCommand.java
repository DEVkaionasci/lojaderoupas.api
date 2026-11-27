package br.com.lojaroupas.command;

import br.com.lojaroupas.service.CepService;
import br.com.lojaroupas.util.HttpUtil;
import br.com.lojaroupas.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/cep/{cep} - consulta um CEP na API externa ViaCEP. */
public class ConsultarCepCommand implements Command {

    private final CepService service;

    public ConsultarCepCommand(CepService service) {
        this.service = service;
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cep = HttpUtil.parametroDaRota(request);
        JsonUtil.escrever(response, HttpServletResponse.SC_OK, service.consultar(cep));
    }
}
