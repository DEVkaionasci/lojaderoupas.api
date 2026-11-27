package br.com.lojaroupas.util;

import br.com.lojaroupas.exception.NegocioException;
import jakarta.servlet.http.HttpServletRequest;

/** Auxiliares para ler dados da requisicao dentro dos Commands. */
public final class HttpUtil {

    /** Atributo onde o Front Controller guarda o trecho variavel da URL (ex.: o 5 em /produtos/5). */
    public static final String ATRIBUTO_PARAMETRO = "parametroDaRota";

    private HttpUtil() {
    }

    public static String parametroDaRota(HttpServletRequest request) {
        Object parametro = request.getAttribute(ATRIBUTO_PARAMETRO);
        if (parametro == null) {
            throw new NegocioException("Parâmetro ausente na URL");
        }
        return parametro.toString();
    }

    public static Long idDaRota(HttpServletRequest request) {
        try {
            return Long.parseLong(parametroDaRota(request));
        } catch (NumberFormatException e) {
            throw new NegocioException("Identificador inválido na URL");
        }
    }
}
