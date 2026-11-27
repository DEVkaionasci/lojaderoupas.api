package br.com.lojaroupas.util;

import br.com.lojaroupas.dto.ErroDTO;
import br.com.lojaroupas.exception.NegocioException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/** Leitura do corpo JSON da requisicao e escrita da resposta JSON. */
public final class JsonUtil {

    private static final Gson GSON = new Gson();

    private JsonUtil() {
    }

    public static <T> T lerCorpo(HttpServletRequest request, Class<T> tipo) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                sb.append(linha);
            }
        }
        if (sb.toString().isBlank()) {
            throw new NegocioException("O corpo da requisição está vazio");
        }
        try {
            T objeto = GSON.fromJson(sb.toString(), tipo);
            if (objeto == null) {
                throw new NegocioException("O corpo da requisição está vazio");
            }
            return objeto;
        } catch (JsonParseException e) {
            throw new NegocioException("JSON inválido no corpo da requisição");
        }
    }

    public static void escrever(HttpServletResponse response, int status, Object corpo) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(GSON.toJson(corpo));
        }
    }

    public static void escreverErro(HttpServletResponse response, int status, String mensagem) throws IOException {
        escrever(response, status, new ErroDTO(status, mensagem, LocalDateTime.now().toString()));
    }
}
