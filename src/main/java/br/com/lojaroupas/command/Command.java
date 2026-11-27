package br.com.lojaroupas.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * PADRAO GoF: COMMAND.
 * Cada acao da API (listar produtos, criar pedido...) e um objeto que implementa
 * esta interface. O Front Controller so sabe chamar executar(); quem decide O QUE
 * fazer e o Command escolhido pela rota. Os Commands fazem o papel de "controladores
 * especificos": leem a requisicao, chamam o Service e escrevem a resposta JSON.
 */
public interface Command {

    void executar(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
